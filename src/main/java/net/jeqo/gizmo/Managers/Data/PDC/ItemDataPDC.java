package net.jeqo.gizmo.Managers.Data.PDC;

import net.jeqo.gizmo.Managers.Data.ItemData;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ItemDataPDC implements PersistentDataType<byte[], ItemData> {

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<ItemData> getComplexType() {
        return ItemData.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull ItemData itemData, @NotNull PersistentDataAdapterContext context) {
        byte[] itemOptionBytes = itemData.getItemOption().getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + Integer.BYTES + itemOptionBytes.length);
        buffer.putInt(itemData.getSlot());
        buffer.putInt(itemOptionBytes.length);
        buffer.put(itemOptionBytes);
        return buffer.array();
    }

    @Override
    public @NotNull ItemData fromPrimitive(byte @NotNull [] bytes, @NotNull PersistentDataAdapterContext context) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int slot = buffer.getInt();
        int itemOptionLength = buffer.getInt();
        byte[] itemOptionBytes = new byte[itemOptionLength];
        buffer.get(itemOptionBytes);
        String itemOption = new String(itemOptionBytes, StandardCharsets.UTF_8);
        return new ItemData(slot, itemOption);
    }
}
