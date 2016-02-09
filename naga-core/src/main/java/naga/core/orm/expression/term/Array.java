package naga.core.orm.expression.term;

import naga.core.orm.expression.Expression;

/**
 * @author Bruno Salmon
 */
public class Array extends UnaryExpression {

    public Array(Expression operand) {
        super(operand);
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        sb.append("[");
        operand.toString(sb);
        return sb.append(']');
    }
}
