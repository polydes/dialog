package com.polydes.dialog.data;

import java.util.Map;

import com.polydes.dialog.app.dataeditors.StructureArgumentEditor;
import com.polydes.dialog.data.def.elements.StructureArgument.StructureArgumentType;

import stencyl.app.api.datatypes.EditorProviders;
import stencyl.app.api.datatypes.EditorProviders.EditorInitializer;
import stencyl.core.datatypes.Types;

import static stencyl.core.api.datatypes.DataType.UNSET_EDITOR;

public class DgTypes
{
    public static StructureArgumentType sat = new StructureArgumentType();
    
    public static void registerTypes()
    {
        Types.get().loadReference(sat);
        EditorProviders.editors.put(sat, Map.of(
            UNSET_EDITOR, (EditorInitializer) (props, sheet, style) -> new StructureArgumentEditor(props, sheet, style)
        ));
    }
    
    public static void unregisterTypes()
    {
        Types.get().unloadReference(sat);
        EditorProviders.editors.remove(sat);
    }
}
