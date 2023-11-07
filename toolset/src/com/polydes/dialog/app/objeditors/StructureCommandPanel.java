package com.polydes.dialog.app.objeditors;

import com.polydes.datastruct.ui.objeditors.StructureObjectPanel;
import com.polydes.dialog.data.DgTypes;
import com.polydes.dialog.data.def.elements.StructureCommand;

import stencyl.app.comp.datatypes.array.StandardArrayEditor;
import stencyl.app.comp.datatypes.string.ExpandingStringEditor;
import stencyl.app.comp.propsheet.PropertiesSheetStyle;
import stencyl.core.datatypes.Types;

public class StructureCommandPanel extends StructureObjectPanel
{
    public StructureCommandPanel(final StructureCommand cmd, PropertiesSheetStyle style)
    {
        super(style, cmd);

        sheet.build()

            .field("name")._editor(Types._String).add()

            .field("description")._editor(ExpandingStringEditor.BUILDER).add()

            .header("Argument")

            .field("args").label("")._editor(StandardArrayEditor.BUILDER).genType(DgTypes.sat).add()

            .finish();

        sheet.addPropertyChangeListener("name", event -> {
            previewKey.setName(cmd.getDisplayLabel());
            preview.lightRefreshLeaf(previewKey);
        });

        sheet.addPropertyChangeListener("description", event -> {
            preview.lightRefreshLeaf(previewKey);
        });

        //XXX: PropertiesSheetSupport doesn't fire propertyChangeEvents for mutable
        //objects that are updated in-place.
        //
        //sheet.addPropertyChangeListener("args", event -> {
        sheet.getField("args").getEditor().addListener(() -> {
            previewKey.setName(cmd.getDisplayLabel());
            preview.lightRefreshLeaf(previewKey);
        });
    }
}
