package com.example.l5memory;

import java.util.ArrayList;
import java.util.List;

public class MemoryManager {
    static { System.loadLibrary("l5memory"); }
    private native long mmapAllocate(int size);

    private long baseAddress;
    private int totalSize;
    private List<MemoryBlock> blocks;

    public static class MemoryBlock {
        long offset;
        int size;
        boolean isFree;

        MemoryBlock(long offset, int size, boolean isFree) {
            this.offset = offset;
            this.size = size;
            this.isFree = isFree;
        }
    }

    public MemoryManager(int size) {
        this.totalSize = size;
        this.baseAddress = mmapAllocate(size);
        this.blocks = new ArrayList<>();
        // Спочатку вся пам'ять вільна
        blocks.add(new MemoryBlock(0, size, true));
    }

    // Алгоритм "First Fit" (Перший підходящий)
    public long malloc(int size) {
        for (MemoryBlock block : blocks) {
            if (block.isFree && block.size >= size) {
                // Якщо блок більший, ніж треба, розбиваємо його
                if (block.size > size) {
                    int remaining = block.size - size;
                    blocks.add(blocks.indexOf(block) + 1,
                            new MemoryBlock(block.offset + size, remaining, true));
                    block.size = size;
                }
                block.isFree = false;
                return baseAddress + block.offset;
            }
        }
        return 0; // Out of memory
    }

    public String getMemoryStatus() {
        StringBuilder sb = new StringBuilder();
        for (MemoryBlock b : blocks) {
            sb.append(b.isFree ? "[FREE: " : "[USED: ").append(b.size).append(" bytes] ");
        }
        return sb.toString();
    }
}