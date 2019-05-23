// Generated by WebFx

module mongoose.server.datasource {

    // Direct dependencies modules
    requires mongoose.shared.domain;
    requires webfx.framework.shared.domain;
    requires webfx.platform.shared.appcontainer;
    requires webfx.platform.shared.datasource;
    requires webfx.platform.shared.json;
    requires webfx.platform.shared.log;
    requires webfx.platform.shared.resource;
    requires webfx.platform.shared.util;

    // Exported packages
    exports mongoose.server.application;

    // Provided services
    provides webfx.platform.shared.services.appcontainer.spi.ApplicationModuleInitializer with mongoose.server.application.MongooseServerDatasourceModuleInitializer;

}