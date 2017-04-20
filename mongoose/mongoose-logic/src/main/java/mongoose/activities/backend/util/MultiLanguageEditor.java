package mongoose.activities.backend.util;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import naga.commons.util.Objects;
import naga.commons.util.Strings;
import naga.commons.util.async.Handler;
import naga.commons.util.function.Callable;
import naga.commons.util.function.Function;
import naga.commons.util.tuples.Pair;
import naga.framework.expression.sqlcompiler.sql.SqlCompiled;
import naga.framework.orm.domainmodel.DataSourceModel;
import naga.framework.orm.entity.Entity;
import naga.framework.orm.entity.EntityList;
import naga.framework.orm.entity.UpdateStore;
import naga.framework.orm.mapping.QueryResultSetToEntityListGenerator;
import naga.framework.ui.controls.LayoutUtil;
import naga.framework.ui.i18n.I18n;
import naga.fx.properties.Properties;
import naga.fxdata.control.HtmlTextEditor;
import naga.platform.services.query.QueryArgument;
import naga.platform.spi.Platform;

import java.util.HashMap;
import java.util.Map;

import static mongoose.actions.MongooseIcons.getLanguageIcon32;
import static naga.framework.ui.action.ActionRegistry.*;
import static naga.framework.ui.controls.LayoutUtil.setPrefSizeToInfinite;

/**
 * @author Bruno Salmon
 */
public class MultiLanguageEditor {

    private final static String[] languages = {"en", "de", "es", "fr", "pt"};
    private final static String entityListId = "entity";

    private final I18n i18n;
    private final Callable entityIdGetter;
    private final DataSourceModel dataSourceModel;
    private final String loadingSelect;
    private final Function<Object, Object> bodyFieldGetter;
    private final Function<Object, Object> subjectFieldGetter;

    private final Map<Object, ToggleButton> languageButtons = new HashMap<>();
    private final ToggleGroup toggleGroup = new ToggleGroup();
    private final BorderPane borderPane = setPrefSizeToInfinite(new BorderPane());
    private Handler<Entity> closeCallback;

    private final Map<Object /*entityId*/, UpdateStore> entityStores = new HashMap<>();
    private final Map<Pair<Object /*entityId**/, Object /*language*/>, MonoLanguageEditor> monoLanguageEditors = new HashMap<>();

    public MultiLanguageEditor(I18n i18n, Entity entity, Function<Object, Object> bodyFieldGetter, Function<Object, Object> subjectFieldGetter) {
        this(i18n, entity::getId, entity.getStore().getDataSourceModel(), bodyFieldGetter, subjectFieldGetter, null);
        registerEntityInStore(entity, null);
    }

    public MultiLanguageEditor(I18n i18n, Callable entityIdGetter, DataSourceModel dataSourceModel, Function<Object, Object> bodyFieldGetter, Function<Object, Object> subjectFieldGetter, String domainClassIdOrLoadingSelect) {
        this.i18n = i18n;
        this.entityIdGetter = entityIdGetter;
        this.dataSourceModel = dataSourceModel;
        this.bodyFieldGetter = bodyFieldGetter;
        this.subjectFieldGetter = subjectFieldGetter;
        StringBuilder sb = domainClassIdOrLoadingSelect == null || domainClassIdOrLoadingSelect.startsWith("select ") ? null : new StringBuilder("select ");
        for (String language : languages) {
            ToggleButton languageButton = new ToggleButton(null, getLanguageIcon32(language));
            languageButton.setUserData(language);
            languageButton.setMinWidth(50d);
            languageButtons.put(language, languageButton);
            if (sb != null) {
                if (!Objects.areEquals(language, languages[0]))
                    sb.append(',');
                sb.append(bodyFieldGetter.apply(language));
                if (subjectFieldGetter != null)
                    sb.append(',').append(subjectFieldGetter.apply(language));
            }
        }
        this.loadingSelect = sb == null ? domainClassIdOrLoadingSelect : sb.append(" from ").append(domainClassIdOrLoadingSelect).append(" where id=?").toString();
        toggleGroup.getToggles().setAll(languageButtons.values());
    }

    public MultiLanguageEditor showOkCancelButton(Handler<Entity> closeCallback) {
        this.closeCallback = closeCallback;
        return this;
    }


    public BorderPane getUiNode() {
        if (toggleGroup.getSelectedToggle() == null) {
            Properties.runOnPropertiesChange(p -> onEntityChanged(), toggleGroup.selectedToggleProperty());
            toggleGroup.selectToggle(languageButtons.get(i18n.getLanguage()));
        }
        return borderPane;
    }

    public void onEntityChanged() {
        MonoLanguageEditor monoLanguageEditor = getCurrentMonoLanguageEditor();
        if (monoLanguageEditor == null)
            return;
        monoLanguageEditor.displayEditor();
        Object entityId = entityIdGetter.call();
        if (entityStores.containsKey(entityId))
            monoLanguageEditor.setEntity(entityStores.get(entityId).getEntityList(entityListId).get(0));
        else if (loadingSelect != null) {
            SqlCompiled sqlCompiled = dataSourceModel.getDomainModel().compileSelect(loadingSelect);
            // Then we ask the query service to execute the sql query
            Platform.getQueryService().executeQuery(new QueryArgument(sqlCompiled.getSql(), new Object[]{entityId}, dataSourceModel.getId())).setHandler(ar -> {
                if (ar.succeeded()) {
                    UpdateStore store = UpdateStore.create(dataSourceModel);
                    Entity entity = QueryResultSetToEntityListGenerator.createEntityList(ar.result(), sqlCompiled.getQueryMapping(), store, entityListId).get(0);
                    registerEntityInStore(entity, store);
                    monoLanguageEditor.setEntity(entity);
                }
            });
        }
    }

    private void registerEntityInStore(Entity entity, UpdateStore store) {
        if (store == null)
            store = UpdateStore.create(dataSourceModel);
        if (entity.getStore() != store)
            entity = store.copyEntity(entity);
        store.markChangesAsCommitted();
        EntityList entityList = store.getOrCreateEntityList(entityListId);
        entityList.clear();
        entityList.add(entity);
        entityStores.put(entityIdGetter.call(), store);
    }

    private MonoLanguageEditor getCurrentMonoLanguageEditor() {
        Object entityId = entityIdGetter.call();
        if (entityId == null)
            return null;
        Toggle selectedLanguageButton = toggleGroup.getSelectedToggle();
        Object language = selectedLanguageButton != null ? selectedLanguageButton.getUserData() : i18n.getLanguage();
        Pair<Object, Object> pair = new Pair<>(entityId, language);
        MonoLanguageEditor monoLanguageEditor = monoLanguageEditors.get(pair);
        if (monoLanguageEditor == null)
            monoLanguageEditors.put(pair, monoLanguageEditor = new MonoLanguageEditor(pair.get2()));
        return monoLanguageEditor;
    }

    private class MonoLanguageEditor {
        private final TextField subjectTextField = new TextField();
        private final HtmlTextEditor editor = new HtmlTextEditor();
        private final Button saveButton =   newAction(closeCallback != null ? OK_ACTION_KEY     : SAVE_ACTION_KEY ,  this::save)  .toButton(i18n);
        private final Button revertButton = newAction(closeCallback != null ? CANCEL_ACTION_KEY : REVERT_ACTION_KEY, this::revert).toButton(i18n);
        private final Object subjectField;
        private final Object bodyField;
        private UpdateStore entityStore;
        private Entity entity;

        MonoLanguageEditor(Object lang) {
            subjectField = subjectFieldGetter == null ? null : subjectFieldGetter.apply(lang);
            bodyField = bodyFieldGetter.apply(lang);
            Properties.runOnPropertiesChange(p -> syncEntityFromUi(), subjectTextField.textProperty(), editor.textProperty());
        }

        void syncEntityFromUi() {
            if (entity != null) {
                if (subjectField != null) {
                    String uiSubject = subjectTextField.getText();
                    String entitySubject = entity.getStringFieldValue(subjectField);
                    if (!Objects.areEquals(uiSubject, entitySubject))
                        entity.setFieldValue(subjectField, uiSubject);
                }
                String uiBody = format(editor.getText());
                String entityBody = entity.getStringFieldValue(bodyField);
                if (uiBody != null && !Objects.areEquals(uiBody, entityBody))
                    entity.setFieldValue(bodyField, uiBody);
                updateButtonsDisable();
            }
        }

        String format(String editorText) {
            if (editorText != null) {
                editorText = Strings.replaceAll(editorText,"\n", "");
                if (editorText.startsWith("<p>") && editorText.indexOf("</p>") == editorText.length() - 4)
                    editorText = editorText.substring(3, editorText.length() - 4);
            }
            return editorText;
        }

        void syncUiFromEntity() {
            if (entity != null) {
                if (subjectField != null) {
                    String uiSubject = subjectTextField.getText();
                    String entitySubject = entity.getStringFieldValue(subjectField);
                    if (!Objects.areEquals(uiSubject, entitySubject))
                        subjectTextField.setText(entitySubject);
                }
                String uiBody = editor.getText();
                String entityBody = entity.getStringFieldValue(bodyField);
                if (!Objects.areEquals(uiBody, entityBody))
                    editor.setText(entityBody);
                updateButtonsDisable();
            }
        }

        void setEntity(Entity entity) {
            if (this.entity != entity) {
                this.entity = entity;
                entityStore = (UpdateStore) entity.getStore();
                syncUiFromEntity();
            }
        }

        void revert() {
            entityStore.cancelChanges();
            syncUiFromEntity();
            callCloseCallback(false);
        }

        void save() {
            entityStore.executeUpdate().setHandler(ar -> {
                if (ar.succeeded()) {
                    updateButtonsDisable();
                    callCloseCallback(true);
                }
            });
        }

        void callCloseCallback(boolean saved) {
            if (closeCallback != null)
                closeCallback.handle(saved ? entity : null);
        }

        void updateButtonsDisable() {
            boolean disable = !entityStore.hasChanges();
            saveButton.setDisable(disable);
            revertButton.setDisable(disable && closeCallback == null);
        }

        void displayEditor() {
            if (borderPane != null && borderPane.getCenter() != editor) {
                syncUiFromEntity();
                if (subjectField != null)
                    borderPane.setTop(subjectTextField);
                borderPane.setCenter(editor);
                BorderPane buttonsBar = new BorderPane();
                HBox hBox = new HBox();
                for (Object language : languages)
                    hBox.getChildren().add(languageButtons.get(language));
                buttonsBar.setLeft(hBox);
                buttonsBar.setCenter(new HBox(20, LayoutUtil.createHGrowable(), saveButton, revertButton, LayoutUtil.createHGrowable()));
                borderPane.setBottom(buttonsBar);
                // The following code is just a temporary workaround to make CKEditor work in html platform (to be removed once fixed)
                if (entity != null) {
                    editor.resize(1, 1);
                    editor.requestLayout();
                }
            }
        }
    }
}