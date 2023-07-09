package com.polydes.dialog.app.dataeditors;

import javax.swing.*;

import org.apache.commons.lang3.ArrayUtils;

import com.polydes.dialog.data.def.elements.StructureArgument;
import com.polydes.dialog.data.def.elements.StructureArgument.Type;

import stencyl.app.api.datatypes.DataEditor;
import stencyl.app.api.datatypes.EditorSheet;
import stencyl.app.comp.datatypes.enumprim.EnumEditor;
import stencyl.app.comp.datatypes.string.SingleLineStringEditor;
import stencyl.app.comp.propsheet.PropertiesSheetStyle;
import stencyl.core.api.datatypes.properties.DataTypeProperties;

public class StructureArgumentEditor extends DataEditor<StructureArgument>
{
    StructureArgument arg;

    final DataEditor<String> nameEditor;
    final DataEditor<Type> typeEditor;

    final JComponent[] comps;

    public StructureArgumentEditor(DataTypeProperties props, EditorSheet sheet, PropertiesSheetStyle style)
    {
        nameEditor = new SingleLineStringEditor(new DataTypeProperties(), sheet, style);
        nameEditor.addListener(() -> updated());

        typeEditor = new EnumEditor<Type>(Type.class);
        typeEditor.addListener(() -> updated());

        comps = ArrayUtils.addAll(nameEditor.getComponents(), typeEditor.getComponents());
    }

    @Override
    public void set(StructureArgument t)
    {
        nameEditor.setValue(t.name);
        typeEditor.setValue(t.type);
    }

    @Override
    public StructureArgument getValue()
    {
        return new StructureArgument(nameEditor.getValue(), typeEditor.getValue());
    }

    @Override
    public JComponent[] getComponents()
    {
        return comps;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        nameEditor.dispose();
        typeEditor.dispose();
    }
}
