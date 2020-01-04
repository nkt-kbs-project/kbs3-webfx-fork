// Generated by WebFx
package java.util;

import java.util.Iterator;
import java.util.logging.Logger;
import webfx.platform.shared.util.function.Factory;

public class ServiceLoader<S> implements Iterable<S> {

    public static <S> ServiceLoader<S> load(Class<S> serviceClass) {
        switch (serviceClass.getName()) {
            case "javafx.application.Application": return new ServiceLoader<S>(mongoose.frontend.application.MongooseFrontendApplication::new);
            case "webfx.framework.client.operations.i18n.ChangeLanguageRequestEmitter": return new ServiceLoader<S>(mongoose.client.operations.i18n.ChangeLanguageToEnglishRequest.ProvidedEmitter::new, mongoose.client.operations.i18n.ChangeLanguageToFrenchRequest.ProvidedEmitter::new);
            case "webfx.framework.client.operations.route.RouteRequestEmitter": return new ServiceLoader<S>();
            case "webfx.framework.client.services.i18n.spi.I18nProvider": return new ServiceLoader<S>(mongoose.client.services.i18n.MongooseI18nProvider::new);
            case "webfx.framework.client.services.push.spi.PushClientServiceProvider": return new ServiceLoader<S>(webfx.framework.client.services.push.spi.impl.simple.SimplePushClientServiceProvider::new);
            case "webfx.framework.client.ui.uirouter.UiRoute": return new ServiceLoader<S>(mongoose.client.activities.login.LoginUiRoute::new, mongoose.client.activities.unauthorized.UnauthorizedUiRoute::new, mongoose.frontend.activities.startbooking.StartBookingUiRoute::new, mongoose.frontend.activities.options.OptionsUiRoute::new, mongoose.frontend.activities.person.PersonUiRoute::new, mongoose.frontend.activities.summary.SummaryUiRoute::new, mongoose.frontend.activities.terms.TermsUiRoute::new, mongoose.frontend.activities.fees.FeesUiRoute::new, mongoose.frontend.activities.program.ProgramUiRoute::new, mongoose.frontend.activities.cart.CartUiRoute::new, mongoose.frontend.activities.contactus.ContactUsUiRoute::new, mongoose.frontend.activities.payment.PaymentUiRoute::new);
            case "webfx.framework.shared.orm.entity.EntityFactoryProvider": return new ServiceLoader<S>(mongoose.shared.entities.impl.AttendanceImpl.ProvidedFactory::new, mongoose.shared.entities.impl.CartImpl.ProvidedFactory::new, mongoose.shared.entities.impl.CountryImpl.ProvidedFactory::new, mongoose.shared.entities.impl.DateInfoImpl.ProvidedFactory::new, mongoose.shared.entities.impl.DocumentImpl.ProvidedFactory::new, mongoose.shared.entities.impl.DocumentLineImpl.ProvidedFactory::new, mongoose.shared.entities.impl.EventImpl.ProvidedFactory::new, mongoose.shared.entities.impl.FilterImpl.ProvidedFactory::new, mongoose.shared.entities.impl.GatewayParameterImpl.ProvidedFactory::new, mongoose.shared.entities.impl.HistoryImpl.ProvidedFactory::new, mongoose.shared.entities.impl.ImageImpl.ProvidedFactory::new, mongoose.shared.entities.impl.ItemFamilyImpl.ProvidedFactory::new, mongoose.shared.entities.impl.ItemImpl.ProvidedFactory::new, mongoose.shared.entities.impl.LabelImpl.ProvidedFactory::new, mongoose.shared.entities.impl.MailImpl.ProvidedFactory::new, mongoose.shared.entities.impl.MethodImpl.ProvidedFactory::new, mongoose.shared.entities.impl.MoneyTransferImpl.ProvidedFactory::new, mongoose.shared.entities.impl.OptionImpl.ProvidedFactory::new, mongoose.shared.entities.impl.OrganizationImpl.ProvidedFactory::new, mongoose.shared.entities.impl.OrganizationTypeImpl.ProvidedFactory::new, mongoose.shared.entities.impl.PersonImpl.ProvidedFactory::new, mongoose.shared.entities.impl.RateImpl.ProvidedFactory::new, mongoose.shared.entities.impl.SiteImpl.ProvidedFactory::new, mongoose.shared.entities.impl.SystemMetricsEntityImpl.ProvidedFactory::new, mongoose.shared.entities.impl.TeacherImpl.ProvidedFactory::new);
            case "webfx.framework.shared.services.authn.spi.AuthenticationServiceProvider": return new ServiceLoader<S>(mongoose.client.services.authn.MongooseAuthenticationServiceProvider::new);
            case "webfx.framework.shared.services.authz.spi.AuthorizationServiceProvider": return new ServiceLoader<S>(mongoose.client.services.authz.MongooseAuthorizationServiceProvider::new);
            case "webfx.framework.shared.services.datasourcemodel.spi.DataSourceModelProvider": return new ServiceLoader<S>(mongoose.shared.services.datasourcemodel.MongooseDataSourceModelProvider::new);
            case "webfx.framework.shared.services.domainmodel.spi.DomainModelProvider": return new ServiceLoader<S>(mongoose.shared.services.domainmodel.MongooseDomainModelProvider::new);
            case "webfx.framework.shared.services.querypush.spi.QueryPushServiceProvider": return new ServiceLoader<S>(webfx.framework.client.jobs.querypush.QueryPushClientServiceProvider::new);
            case "webfx.kit.launcher.spi.WebFxKitLauncherProvider": return new ServiceLoader<S>(webfx.kit.launcher.spi.gwt.GwtWebFxKitLauncherProvider::new);
            case "webfx.kit.mapper.spi.WebFxKitMapperProvider": return new ServiceLoader<S>(webfx.kit.mapper.spi.gwt.GwtWebFxKitHtmlMapperProvider::new);
            case "webfx.platform.client.services.storage.spi.LocalStorageProvider": return new ServiceLoader<S>(webfx.platform.gwt.services.storage.spi.impl.GwtLocalStorageProvider::new);
            case "webfx.platform.client.services.storage.spi.SessionStorageProvider": return new ServiceLoader<S>(webfx.platform.gwt.services.storage.spi.impl.GwtSessionStorageProvider::new);
            case "webfx.platform.client.services.uischeduler.spi.UiSchedulerProvider": return new ServiceLoader<S>(webfx.platform.gwt.services.uischeduler.spi.impl.GwtUiSchedulerProvider::new);
            case "webfx.platform.client.services.websocket.spi.WebSocketServiceProvider": return new ServiceLoader<S>(webfx.platform.gwt.services.websocket.spi.impl.GwtWebSocketServiceProvider::new);
            case "webfx.platform.client.services.windowhistory.spi.WindowHistoryProvider": return new ServiceLoader<S>(webfx.platform.web.services.windowhistory.spi.impl.WebWindowHistoryProvider::new);
            case "webfx.platform.client.services.windowlocation.spi.WindowLocationProvider": return new ServiceLoader<S>(webfx.platform.gwt.services.windowlocation.spi.impl.GwtWindowLocationProvider::new);
            case "webfx.platform.gwt.services.resource.spi.impl.GwtResourceBundle": return new ServiceLoader<S>(mongoose.frontend.application.gwt.embed.EmbedResourcesBundle.ProvidedGwtResourceBundle::new);
            case "webfx.platform.shared.services.appcontainer.spi.ApplicationContainerProvider": return new ServiceLoader<S>(webfx.platform.gwt.services.appcontainer.spi.impl.GwtApplicationContainerProvider::new);
            case "webfx.platform.shared.services.appcontainer.spi.ApplicationJob": return new ServiceLoader<S>(mongoose.client.jobs.sessionrecorder.ClientSessionRecorderJob::new, webfx.framework.client.jobs.querypush.QueryPushClientJob::new);
            case "webfx.platform.shared.services.appcontainer.spi.ApplicationModuleInitializer": return new ServiceLoader<S>(webfx.kit.launcher.WebFxKitLauncherModuleInitializer::new, webfx.platform.shared.services.appcontainer.spi.impl.ApplicationJobsStarter::new, webfx.platform.shared.services.buscall.BusCallModuleInitializer::new, webfx.platform.shared.services.serial.SerialCodecModuleInitializer::new, mongoose.client.operationactionsloading.MongooseClientOperationActionsLoader::new, webfx.framework.shared.interceptors.dqlsubmit.DqlSubmitInterceptorModuleInitializer::new, webfx.framework.shared.interceptors.dqlquery.DqlQueryInterceptorModuleInitializer::new, webfx.platform.gwt.services.resource.spi.impl.GwtResourceModuleInitializer::new);
            case "webfx.platform.shared.services.bus.spi.BusServiceProvider": return new ServiceLoader<S>(webfx.platform.client.services.websocketbus.web.WebWebsocketBusServiceProvider::new);
            case "webfx.platform.shared.services.buscall.spi.BusCallEndpoint": return new ServiceLoader<S>(webfx.platform.shared.services.submit.ExecuteSubmitBusCallEndpoint::new, webfx.platform.shared.services.submit.ExecuteSubmitBatchBusCallEndpoint::new, webfx.platform.shared.services.query.ExecuteQueryBusCallEndpoint::new, webfx.platform.shared.services.query.ExecuteQueryBatchBusCallEndpoint::new, webfx.framework.shared.services.querypush.ExecuteQueryPushBusCallEndpoint::new);
            case "webfx.platform.shared.services.datasource.spi.LocalDataSourceProvider": return new ServiceLoader<S>();
            case "webfx.platform.shared.services.json.spi.JsonProvider": return new ServiceLoader<S>(webfx.platform.gwt.services.json.spi.impl.GwtJsonObject::create);
            case "webfx.platform.shared.services.log.spi.LoggerProvider": return new ServiceLoader<S>(webfx.platform.gwt.services.log.spi.impl.GwtLoggerProvider::new);
            case "webfx.platform.shared.services.query.spi.QueryServiceProvider": return new ServiceLoader<S>(webfx.platform.shared.services.query.spi.impl.remote.RemoteQueryServiceProvider::new);
            case "webfx.platform.shared.services.resource.spi.ResourceServiceProvider": return new ServiceLoader<S>(webfx.platform.gwt.services.resource.spi.impl.GwtResourceServiceProvider::new);
            case "webfx.platform.shared.services.scheduler.spi.SchedulerProvider": return new ServiceLoader<S>(webfx.platform.gwt.services.uischeduler.spi.impl.GwtUiSchedulerProvider::new);
            case "webfx.platform.shared.services.serial.spi.SerialCodec": return new ServiceLoader<S>(webfx.platform.shared.services.submit.SubmitArgument.ProvidedSerialCodec::new, webfx.platform.shared.services.submit.SubmitResult.ProvidedSerialCodec::new, webfx.platform.shared.services.submit.GeneratedKeyBatchIndex.ProvidedSerialCodec::new, webfx.platform.shared.services.buscall.BusCallArgument.ProvidedSerialCodec::new, webfx.platform.shared.services.buscall.BusCallResult.ProvidedSerialCodec::new, webfx.platform.shared.services.buscall.SerializableAsyncResult.ProvidedSerialCodec::new, webfx.platform.shared.services.serial.spi.impl.ProvidedBatchSerialCodec::new, webfx.platform.shared.services.query.QueryArgument.ProvidedSerialCodec::new, webfx.platform.shared.services.query.QueryResult.ProvidedSerialCodec::new, webfx.framework.shared.services.querypush.QueryPushArgument.ProvidedSerialCodec::new, webfx.framework.shared.services.querypush.QueryPushResult.ProvidedSerialCodec::new, webfx.framework.shared.services.querypush.diff.impl.QueryResultTranslation.ProvidedSerialCodec::new);
            case "webfx.platform.shared.services.shutdown.spi.ShutdownProvider": return new ServiceLoader<S>(webfx.platform.gwt.services.shutdown.spi.impl.GwtShutdownProvider::new);
            case "webfx.platform.shared.services.submit.spi.SubmitServiceProvider": return new ServiceLoader<S>(webfx.platform.shared.services.submit.spi.impl.remote.RemoteSubmitServiceProvider::new);
            case "webfx.platform.web.services.windowhistory.spi.impl.JsWindowHistory": return new ServiceLoader<S>(webfx.platform.gwt.services.windowhistory.spi.impl.GwtJsWindowHistory::new);
            // UNKNOWN SPI
            default:
               Logger.getLogger(ServiceLoader.class.getName()).warning("Unknown " + serviceClass + " SPI - returning no provider");
               return new ServiceLoader<S>();
        }
    }

    private final Factory[] factories;

    public ServiceLoader(Factory... factories) {
        this.factories = factories;
    }

    public Iterator<S> iterator() {
        return new Iterator<S>() {
            int index = 0;
            @Override
            public boolean hasNext() {
                return index < factories.length;
            }

            @Override
            public S next() {
                return (S) factories[index++].create();
            }
        };
    }
}