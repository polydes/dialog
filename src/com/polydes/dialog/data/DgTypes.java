package com.polydes.dialog.data;

import java.util.Map;

import com.polydes.dialog.data.def.elements.StructureArgument.StructureArgumentEditor;
import com.polydes.dialog.data.def.elements.StructureArgument.StructureArgumentType;

import stencyl.core.datatypes.Types;
import stencyl.toolset.api.datatypes.EditorProviders;
import stencyl.toolset.api.datatypes.EditorProviders.EditorInitializer;

import static stencyl.core.api.datatypes.DataType.UNSET_EDITOR;

public class DgTypes
{
    public static StructureArgumentType sat = new StructureArgumentType();
    
    public static void registerTypes()
    {
        Types.get().loadReference(sat);
        EditorProviders.editors.put(sat, Map.of(
            UNSET_EDITOR, (EditorInitializer) StructureArgumentEditor::new
        ));
    }
    
    public static void unregisterTypes()
    {
        Types.get().unloadReference(sat);
        EditorProviders.editors.remove(sat);
    }
}
