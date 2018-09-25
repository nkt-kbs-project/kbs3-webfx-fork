package webfx.framework.expression.builder.terms;

import webfx.framework.expression.Expression;
import webfx.framework.expression.builder.BuilderThreadContext;
import webfx.framework.expression.lci.ParserDomainModelReader;

/**
 * @author Bruno Salmon
 */
public abstract class ExpressionBuilder {

    public String buildingClassName;
    public Object buildingClass;

    public abstract Expression build();

    protected void propagateDomainClasses() {
        if (buildingClass == null && buildingClassName != null)
            buildingClass = getModelReader().getDomainClassByName(buildingClassName);
    }

    protected static ParserDomainModelReader getModelReader() {
        BuilderThreadContext context = BuilderThreadContext.getInstance();
        return context == null ? null : context.getModelReader();
    }
}