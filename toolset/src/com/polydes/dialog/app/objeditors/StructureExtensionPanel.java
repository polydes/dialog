package com.polydes.dialog.app.objeditors;

import com.polydes.datastruct.ui.objeditors.StructureObjectPanel;
import com.polydes.dialog.data.def.elements.StructureExtension;

import stencyl.app.comp.datatypes.string.ExpandingStringEditor;
import stencyl.app.comp.propsheet.PropertiesSheetStyle;
import stencyl.core.datatypes.Types;

public class StructureExtensionPanel extends StructureObjectPanel
{
    public StructureExtensionPanel(final StructureExtension extension, PropertiesSheetStyle style)
    {
        super(style, extension);

        sheet.build()

            .field("implementation")._editor(Types._String).add()

            .field("description")._editor(ExpandingStringEditor.BUILDER).add()
            .onUpdate(() -> preview.lightRefreshLeaf(previewKey))

            .finish();
    }
}
