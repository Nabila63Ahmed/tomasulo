package tomasulo.memory;

import tomasulo.configuration.memory.CacheConfig;
import tomasulo.configuration.memory.WritingPolicy;

import java.util.HashMap;
import java.util.Objects;
import java.util.StringJoiner;

public class Cache {

    private static final int OFFSET = 0;
    private static final int TAG = 1;
    private static final int INDEX = 2;

    private WritingPolicy writeMissPolicy;
    private WritingPolicy writeHitPolicy;
    private CacheEntry[] entries;
    private int associativity;

    private int offsetBits;
    private int indexBits;
    private int tagBits;
    private int cacheLines;

    public Cache(CacheConfig config) {

        this.writeMissPolicy = config.getWriteMissPolicy();
        this.writeHitPolicy = config.getWriteHitPolicy();
        this.cacheLines = config.getSizeBytes() / config.getLineSizeBytes();
        this.associativity = config.getAssociativity();
        this.offsetBits = log2(config.getLineSizeBytes() / 2);
        this.indexBits = log2(cacheLines / this.associativity);
        this.tagBits = (config.getLineSizeBytes() * 8) - (this.offsetBits + this.indexBits);
        entries = new CacheEntry[cacheLines];

        for (int i = 0; i < entries.length; i++) {
            entries[i] = new CacheEntry(null, 0, false);
        }

    }

    public static int log2(int n) {
        return (int) (Math.log(n) / Math.log(2));
    }

    public static void testDirectMap() {
        CacheConfig config = new CacheConfig(128, 4, 1, 10, WritingPolicy.THROUGH, WritingPolicy.BACK);
        Cache cache = new Cache(config);
        Block block = null;
        for (int i = 0; i < 20; i += 2) {
            block = new Block(2);
            block.addData(i, 0);
            block.addData(i + 1, 1);
            System.out.println("Write (" + i + "): " + cache.write(i, block));
        }
        System.out.println(cache);
    }

    public static void testDirectReplace() {
        CacheConfig config = new CacheConfig(128, 4, 1, 10, WritingPolicy.BACK, WritingPolicy.BACK);
        Cache cache = new Cache(config);
        Block block = null;
//        for (int i = 0; i < 64; i += 2) {
//            block = new Block(2);
//            block.addData(i, 0);
//            block.addData(i + 1, 1);
////            cache.write(i, block);
//            System.out.println("Write (" + i + "): " + cache.write(i, block));
//        }
//        for (int i = 64; i < 128; i += 2) {
//            block = new Block(2);
//            block.addData(i * 2, 0);
//            block.addData(i * 2 + 1, 1);
////            cache.write(i, block);
//            System.out.println("Write (" + i + "): " + cache.write(i, block));
//        }

        block = new Block(2);
        block.addData(128, 0);
        block.addData(129, 1);
        System.out.println(cache.write(4, block));
        block = new Block(2);
        block.addData(208, 0);
        block.addData(100, 1);
        System.out.println(cache.write(4, block));


        System.out.println(cache);
    }

    public static void testFullAssociativity() {
        CacheConfig config = new CacheConfig(128, 4, 32, 10, WritingPolicy.BACK, WritingPolicy.BACK);
        Cache cache = new Cache(config);
        Block block = null;
        for (int i = 0; i < 128; i += 2) {
            block = new Block(2);
            block.addData(i, 0);
            block.addData(i + 1, 1);
            System.out.println("Write (" + i + "): " + cache.write(i, block));
        }

        block = new Block(2);
        block.addData(500, 0);
        block.addData(501, 1);
        System.out.println(cache.write(4, block));
        block = new Block(2);
        block.addData(600, 0);
        block.addData(601, 1);
        System.out.println(cache.write(6, block));


        System.out.println(cache);
    }

    public static void testSetAssociativity(int associativity) {
        CacheConfig config = new CacheConfig(128, 4, associativity, 10, WritingPolicy.THROUGH, WritingPolicy.THROUGH);
        Cache cache = new Cache(config);
        Block block = null;
        for (int i = 16; i < 17; i += 1) {
            block = new Block(2);
            block.addData(i, 0);
            block.addData(i + 1, 1);
            System.out.println("Write (" + i + "): " + cache.write(i, block));
        }
        System.out.println(cache);
    }

    public static void main(String[] args) {
//        testDirectMap();
//        testDirectReplace();
//        testFullAssociativity();
//        testSetAssociativity(2);
//        System.out.println(reconstructOldAddress(5, 6));
    }


    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < entries.length; i++) {
            s += i + " -> \n" + entries[i] + "\n";
        }
        return s;
    }

    public  HashMap<Integer, Integer> convertAddress(int addressWords) {
        HashMap<Integer, Integer> map = new HashMap<>();
        String binaryAddress = String.format("%16s", Integer.toBinaryString(addressWords)).replace(' ', '0');
        String tagBinary, indexBinary, offsetBinary;
        int offsetDecimal, indexDecimal, tagDecimal;

        offsetBinary = binaryAddress.substring(binaryAddress.length() - offsetBits);
        indexBinary = binaryAddress.substring(binaryAddress.length() - (offsetBits + indexBits), binaryAddress.length() - offsetBits);
        tagBinary = binaryAddress.substring(0, binaryAddress.length() - (offsetBits + indexBits));

        offsetDecimal = Integer.parseInt(offsetBinary, 2);
        indexDecimal = Objects.equals(indexBinary, "") ? 0 : Integer.parseInt(indexBinary, 2);
        tagDecimal = Integer.parseInt(tagBinary, 2);

        map.put(TAG, tagDecimal);
        map.put(INDEX, indexDecimal);
        map.put(OFFSET, offsetDecimal);

        return map;
    }


    private HashMap<String, Object> convertBlockToHashMap(Block block, int address, boolean oldAddressEnabler){
        HashMap<String, Object> map = new HashMap<>();
        map.put("address", address);
        map.put("block", block);
        map.put("enable", oldAddressEnabler);
        return map;
    }
    private HashMap<String, Object> writeDirectMapped(int entryIndex, int entryTag, Block block) {

        CacheEntry entry = this.entries[entryIndex];
//        System.out.println("Ahmed \n" + entry.getBlock());
        if (entry.isValid() && entry.getTag() == entryTag) {
            if (this.writeHitPolicy == WritingPolicy.BACK) {
                entry.setBlock(block);
                entry.setDirty(true);
                return null;
            } else {
//                Block oldBlock = entry.getBlock();
                entry.setBlock(block);
                entry.setDirty(false);
//                return oldBlock;
                return convertBlockToHashMap(block, 0, false);
            }
        } else {
            if (this.writeMissPolicy == WritingPolicy.BACK) {
                if (entry.isDirty()) {
                    boolean isValid = entry.isValid();
                    Block oldBlock = entry.getBlock();
                    int oldAdress = reconstructOldAddress(entryIndex, entry.getTag());
                    entry.setBlock(block);
                    entry.setTag(entryTag);
                    entry.setValid(true);
                    entry.setDirty(true);
                    return isValid ? convertBlockToHashMap(oldBlock, oldAdress, true): null;
                } else {
                    entry.setBlock(block);
                    entry.setTag(entryTag);
                    entry.setValid(true);
                    entry.setDirty(true);
                    return null;
                }
            } else {
//                Block oldBlock = entry.getBlock();
                entry.setBlock(block);
                entry.setTag(entryTag);
                entry.setValid(true);
                entry.setDirty(false);
                return convertBlockToHashMap(block, 0, false);
            }
        }
    }

    private Integer reconstructOldAddress(int indexDecimal, int tagDecimal) {

        String address = "";

        address += Integer.toBinaryString(indexDecimal);
        address += Integer.toBinaryString(tagDecimal);


        for (int i = 0; i < this.offsetBits; i++) {
            address += "0";
        }
        return Integer.parseInt(address, 2);

    }

    private HashMap<String, Object> writeAssociativeHelper(CacheEntry entry, int entryTag, Block block) {
        if (entry.isValid() && entry.getTag() == entryTag) {
            if (this.writeHitPolicy == WritingPolicy.BACK) {
                entry.setBlock(block);
                entry.setDirty(true);
                return null;
            } else {
//                Block oldBlock = entry.getBlock();
                entry.setBlock(block);
                entry.setDirty(false);
//                return oldBlock;
                return convertBlockToHashMap(block, 0, false);
            }
        } else {
            if (this.writeMissPolicy == WritingPolicy.BACK) {
                if (entry.isDirty()) {
                    boolean isValid = entry.isValid();
                    Block oldBlock = entry.getBlock();
                    //FIXME: send me the set index please
//                    int oldAdress = reconstructOldAddress(entryIndex, entry.getTag());
                    entry.setBlock(block);
                    entry.setTag(entryTag);
                    entry.setValid(true);
                    entry.setDirty(true);
                    return isValid ? convertBlockToHashMap(oldBlock,0, false ): null;
                } else {
                    entry.setBlock(block);
                    entry.setTag(entryTag);
                    entry.setValid(true);
                    entry.setDirty(true);
                    return null;
                }
            } else {
//                Block oldBlock = entry.getBlock();
                entry.setBlock(block);
                entry.setTag(entryTag);
                entry.setValid(true);
                entry.setDirty(false);
                return convertBlockToHashMap(block,0, false );
            }
        }
    }

    private HashMap<String, Object> writeFullyAssociative(int entryTag, Block block) {
        CacheEntry entry = null;
        for (int i = 0; i < this.associativity; i++) {
            entry = entries[i];
            if (entry.getTag() == entryTag) {
                return writeAssociativeHelper(entry, entryTag, block);
            }
        }
        for (int i = 0; i < this.associativity; i++) {
            entry = entries[i];
            if (!entry.isValid()) {
                entry.setBlock(block);
                entry.setTag(entryTag);
                entry.setValid(true);
                if (this.writeMissPolicy == WritingPolicy.BACK) {
                    entry.setDirty(true);
                    return null;
                } else {
                    entry.setDirty(false);
                    return convertBlockToHashMap(block,0, false );
                }
            }
        }
        Block oldBlock = entry.getBlock();
        entry.setBlock(block);
        entry.setTag(entryTag);
        entry.setValid(true);
        if (this.writeMissPolicy == WritingPolicy.BACK) {
            boolean isDirty = entry.isDirty();
            entry.setDirty(true);
            //FIXME: old address should be cacl here
            return isDirty ? convertBlockToHashMap(oldBlock, 0, false) : null;
        } else {
            entry.setDirty(false);
            return convertBlockToHashMap(block, 0, false) ;
        }
    }

    private HashMap<String,Object> writeSetAssociative(int setIndex, int entryTag, Block block) {
        CacheEntry entry = null;
        for (int i = setIndex * associativity; i < (setIndex * associativity) + associativity; i++) {
            entry = this.entries[i];
            if (entry.getTag() == entryTag) {
                return writeAssociativeHelper(entry, entryTag, block);
            }
        }
        for (int i = setIndex * associativity; i < (setIndex * associativity) + associativity; i++) {
            entry = this.entries[i];
            if (!entry.isValid()) {
                entry.setBlock(block);
                entry.setTag(entryTag);
                entry.setValid(true);
                if (this.writeMissPolicy == WritingPolicy.BACK) {
                    entry.setDirty(true);
                    return null;
                } else {
                    entry.setDirty(false);
                    return convertBlockToHashMap(block, 0, false) ;
                }
            }
        }
        Block oldBlock = entry.getBlock();
        int oldAddress = reconstructOldAddress(setIndex, entryTag);
        entry.setBlock(block);
        entry.setTag(entryTag);
        entry.setValid(true);
        if (this.writeMissPolicy == WritingPolicy.BACK) {
            boolean isDirty = entry.isDirty();
            entry.setDirty(true);

            return isDirty ? convertBlockToHashMap(oldBlock, oldAddress, true): null;
        } else {
            entry.setDirty(false);
            return convertBlockToHashMap(block, 0, false);
        }
    }

    public HashMap<String, Object> write(int addressWords, Block block) {
        HashMap<Integer, Integer> map = convertAddress(addressWords);
        int offsetDecimal, indexDecimal, tagDecimal;
        offsetDecimal = map.get(OFFSET);
        indexDecimal = map.get(INDEX);
        tagDecimal = map.get(TAG);

        if (associativity == 1) {
            return writeDirectMapped(indexDecimal, tagDecimal, block);
        } else {
            if (associativity != this.cacheLines)
                return writeSetAssociative(indexDecimal, tagDecimal, block);
            else
                return writeFullyAssociative(tagDecimal, block);

        }
    }

    public void writeTrivial(int addressWords, Block block) {
        HashMap<Integer, Integer> map = convertAddress(addressWords);
        int indexDecimal = map.get(INDEX);
        int tagDecimal = map.get(TAG);
        CacheEntry entry = this.entries[indexDecimal];
        entry.setBlock(block);
        entry.setValid(true);
        entry.setDirty(false);
        entry.setTag(tagDecimal);
    }

    public Block read(int addressWords) {
        HashMap<Integer, Integer> map = convertAddress(addressWords);
        int offsetDecimal, indexDecimal, tagDecimal;

        offsetDecimal = map.get(OFFSET);
        indexDecimal = map.get(INDEX);
        tagDecimal = map.get(TAG);
        if (associativity == 1) {
            CacheEntry entry = this.entries[indexDecimal];
            if (entry.isValid() && entry.getTag() == tagDecimal) return entry.getBlock();
        } else if (associativity == this.cacheLines) {
            for (int i = 0; i < entries.length; i++) {
                CacheEntry entry = entries[i];
                if (entry.isValid() && entry.getTag() == tagDecimal) return entry.getBlock();
            }
        } else {
            CacheEntry entry;
            for (int i = indexDecimal * associativity; i < (indexDecimal * associativity) + associativity; i++) {
                entry = this.entries[i];
                if (entry.getTag() == tagDecimal) {
                    return this.entries[i].getBlock();
                }
            }
        }

        return null;
    }

}
