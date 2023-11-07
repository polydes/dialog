package com.polydes.dialog.app.objeditors;

import com.polydes.datastruct.ui.objeditors.StructureObjectPanel;
import com.polydes.dialog.data.def.elements.StructureDrawkey;

import stencyl.app.comp.propsheet.PropertiesSheetStyle;
import stencyl.core.datatypes.Types;

public class StructureDrawkeyPanel extends StructureObjectPanel
{
    public StructureDrawkeyPanel(final StructureDrawkey drawkey, PropertiesSheetStyle style)
    {
        super(style, drawkey);

        sheet.build()

            .field("name")._editor(Types._String).add()

            .finish();

        sheet.addPropertyChangeListener(event -> {
            preview.lightRefreshLeaf(previewKey);
        });
    }
}
