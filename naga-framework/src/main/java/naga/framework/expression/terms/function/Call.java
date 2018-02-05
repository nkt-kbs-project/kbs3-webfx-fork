package naga.framework.expression.terms.function;

import naga.util.Arrays;
import naga.framework.expression.Expression;
import naga.framework.expression.lci.DataReader;
import naga.framework.expression.terms.ExpressionArray;
import naga.framework.expression.terms.Ordered;
import naga.framework.expression.terms.UnaryExpression;
import naga.type.Type;

import java.util.Collection;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public class Call<T> extends UnaryExpression<T> {

    private final String functionName;
    private final Function function;
    private final ExpressionArray orderBy; // used only for aggregate functions

    public Call(String functionName, Expression argument) {
        this(functionName, argument, null);
    }

    public Call(String functionName, Expression argument, ExpressionArray orderBy) {
        super(argument);
        this.functionName = functionName;
        function = Function.getFunction(functionName);
        if (function == null)
            throw new IllegalArgumentException("Unknown function: " + functionName);
        this.orderBy = orderBy;
        /*
        if ("readOnly".equals(functionName))
            label = PropertyNameExpression.getTopRightExpression(argument).getLabel();*/
    }

    public String getFunctionName() {
        return functionName;
    }

    public Function getFunction() {
        return function;
    }

    public ExpressionArray getOrderBy() {
        return orderBy;
    }

    @Override
    public Type getType() {
        Type type = function.getReturnType();
        if (type == null) { // type = null means the function returns the same type as the argument (ex: sum function)
            Expression operand = getOperand(); // General case: the type to return is the type of the operand
            if (operand instanceof ExpressionArray) { // Particular case: multiple arguments (ex: coalesce(ar1, arg2))
                Expression[] arguments = ((ExpressionArray) operand).getExpressions();
                if (!Arrays.isEmpty(arguments))
                    operand = arguments[0]; // we return the type of the first argument
            }
            type = operand.getType();
        }
        return type;
    }

    @Override
    public Object evaluate(T domainObject, DataReader<T> dataReader) {
        if (function.isEvaluable()) {
            if (!(function instanceof AggregateFunction))
                return function.evaluate(operand == null ? null : operand.evaluate(domainObject, dataReader), dataReader);
            Object primaryKey = dataReader.prepareValueBeforeTypeConversion(domainObject, null);
            if (primaryKey instanceof AggregateKey) {
                AggregateKey aggregateKey = (AggregateKey) primaryKey;
                if (orderBy != null)
                    orderBy(aggregateKey.getAggregates(), dataReader, orderBy);
                return ((AggregateFunction) function).evaluateOnAggregates(domainObject, aggregateKey.getAggregates().toArray(), operand, dataReader);
            }
        }
        return dataReader.getDomainFieldValue(domainObject, this);
        //return ThreadContext.getContext().getWorkingData().getValue(id, this /* RawData will return stored value where fieldId = this.getId() = functionName */);
    }

    @Override
    public int getPrecedenceLevel() {
        return function.getPrecedenceLevel();
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        sb.append(functionName);
        if (!function.isKeyword()) {
            sb.append('(');
            if (operand != null)
                operand.toString(sb);
            if (orderBy != null)
                orderBy.toString(sb.append(" order by "));
            sb.append(')');
        }
        return sb;
    }

    public void collectPersistentTerms(Collection<Expression<T>> persistentTerms) {
        if (function.isSqlExpressible())
            persistentTerms.add(this);
        else if (operand != null)
            operand.collectPersistentTerms(persistentTerms);
    }

    static <T> List<T> orderBy(List<T> list, DataReader<T> dataReader, Expression<T>... orderExpressions) {
        if (orderExpressions.length == 1 && orderExpressions[0] instanceof ExpressionArray)
            orderBy(list, dataReader, ((ExpressionArray) orderExpressions[0]).getExpressions());
        else java.util.Collections.sort(list, (e1, e2) -> {
            if (e1 == e2)
                return 0;
            if (e1 == null)
                return 1;
            if (e2 == null)
                return -1;
            for (Expression<T> orderExpression : orderExpressions) {
                Object o1 = orderExpression.evaluate(e1, dataReader);
                Object o2 = orderExpression.evaluate(e2, dataReader);
                Ordered<T> ordered = orderExpression instanceof Ordered ? (Ordered<T>) orderExpression : null;
                int result;
                if (o1 == o2)
                    result = 0;
                else if (o1 == null || !(o1 instanceof Comparable))
                    result = ordered == null || ordered.isNullsLast() ? 1 : -1;
                else if (o2 == null || !(o2 instanceof Comparable))
                    result = ordered == null || ordered.isNullsLast() ? -1 : 1;
                else {
                    result = ((Comparable) o1).compareTo(o2);
                    if (ordered != null && ordered.isDescending())
                        result = -result;
                }
                if (result != 0)
                    return result;
            }
            return 0;
        });
        return list;
    }

}
