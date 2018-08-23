package naga.framework.expression.sqlcompiler.terms;

import naga.framework.expression.Expression;
import naga.framework.expression.sqlcompiler.ExpressionSqlCompiler;
import naga.framework.expression.terms.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public abstract class AbstractTermSqlCompiler<E extends Expression> {

    private final Class<? extends Expression>[] supportedTermClasses;

    public AbstractTermSqlCompiler(Class<? extends Expression>... supportedTermClasses) {
        this.supportedTermClasses = supportedTermClasses;
    }

    public Class<? extends Expression>[] getSupportedTermClasses() {
        return supportedTermClasses;
    }

    public abstract void compileExpressionToSql(E e, Options o);

    protected void compileChildExpressionToSql(Expression e, Options o) {
        ExpressionSqlCompiler.compileExpression(e, o);
    }

    protected void compileExpressionPersistentTermsToSql(Expression e, Options o) {
        List<Expression> persistentTerms = new ArrayList<>();
        e.collectPersistentTerms(persistentTerms);
        for (Expression term : persistentTerms)
            compileChildExpressionToSql(term, o);
    }

    protected void compileSelect(Select select, Options o) {
        ExpressionSqlCompiler.buildSelect(select, o);
    }

}