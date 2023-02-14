package com.akroZora.highendtechnology.recipe;

import com.akroZora.highendtechnology.HighEndTechnology;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;

import java.util.Map;
import java.util.Set;

public class AssemblyStationRecipe implements Recipe<CraftingContainer>, Container {
    static int MAX_WIDTH = 3;
    static int MAX_HEIGHT = 3;


    public static void setCraftingSize(int width, int height) {
        if (MAX_WIDTH < width) MAX_WIDTH = width;
        if (MAX_HEIGHT < height) MAX_HEIGHT = height;
    }

    final int width;
    final int height;
    final NonNullList<Ingredient> patternIngredients;
    final NonNullList<Ingredient> assemblyIngredients;
    private final int[] assemblyCount;
    final ItemStack result;
    private final ResourceLocation id;
    final String group;


    public AssemblyStationRecipe(ResourceLocation pId, String pGroup, int pWidth, int pHeight, NonNullList<Ingredient> pattern, NonNullList<Ingredient> assemblyItems,int[] assemblyCount, ItemStack pResult) {
        this.id = pId;
        this.group = pGroup;
        this.width = pWidth;
        this.height = pHeight;
        this.patternIngredients = pattern;
        this.assemblyIngredients = assemblyItems;
        this.assemblyCount = assemblyCount;
        this.result = pResult;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHAPED_RECIPE;
    }

    public int getAssemblyCount(int slot) {
        return assemblyCount[slot];
    }

    public void setAssemblyCount(int slot, int assemblyCount) {
        this.assemblyCount[slot] = assemblyCount;
    }

    public String getGroup() {
        return this.group;
    }

    public ItemStack getResultItem() {
        return this.result;
    }
    public NonNullList<Ingredient> getAssemblyIngredients() {
        return this.assemblyIngredients;
    }
    public NonNullList<Ingredient> getPatternIngredients() {
        return this.patternIngredients;
    }
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth >= this.width && pHeight >= this.height;
    }


    public boolean matches(CraftingContainer pInv, Level pLevel) {
        if(!assemblyMatches(pInv)){
            return false;
        }
        for(int i = 0; i <= pInv.getWidth() - this.width; ++i) {
            for(int j = 0; j <= pInv.getHeight() - this.height; ++j) {
                if (this.patternMatches(pInv, i, j, true)) {
                    return true;
                }

                if (this.patternMatches(pInv, i, j, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean assemblyMatches(CraftingContainer container) {
        int emptySlotsRequired = 3-this.assemblyIngredients.size();
        int emptySlots = 0;
        for (int i = 0; i < 3; i++) {
            if(container.getItem(i+9).isEmpty()){
                emptySlots++;
            }
        }
        if(emptySlotsRequired!=emptySlots){
            return false;
        }
        for(int i = 0; i<this.assemblyIngredients.size(); i++) {
            boolean match = false;
            for(int j=0;j<3;j++) {
                if(this.assemblyIngredients.get(i).test(container.getItem(j+9))&&this.assemblyCount[i]<=container.getItem(j+9).getCount()){
                    match = true;
                }
            }
            if(!match){
                return false;
            }
        }
        return true;
    }

    private boolean patternMatches(CraftingContainer pCraftingInventory, int pWidth, int pHeight, boolean pMirrored) {
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                int k = i - pWidth;
                int l = j - pHeight;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < this.width && l < this.height) {
                    if (pMirrored) {
                        ingredient = this.patternIngredients.get(this.width - k - 1 + l * this.width);
                    } else {
                        ingredient = this.patternIngredients.get(k + l * this.width);
                    }
                }
                if (!ingredient.test(pCraftingInventory.getItem(i + j * 3))) {
                    return false;
                }
            }
        }

        return true;
    }

    public ItemStack assemble(CraftingContainer pInv) {
        return this.getResultItem().copy();
    }

    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }

    static NonNullList<Ingredient> dissolvePattern(String[] pPattern, Map<String, Ingredient> pKeys, int pPatternWidth, int pPatternHeight) {
        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(pPatternWidth * pPatternHeight, Ingredient.EMPTY);
        Set<String> set = Sets.newHashSet(pKeys.keySet());
        set.remove(" ");

        for(int i = 0; i < pPattern.length; ++i) {
            for(int j = 0; j < pPattern[i].length(); ++j) {
                String s = pPattern[i].substring(j, j + 1);
                Ingredient ingredient = pKeys.get(s);
                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
                }

                set.remove(s);
                nonnulllist.set(j + pPatternWidth * i, ingredient);
            }
        }

        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        } else {
            return nonnulllist;
        }
    }

    @VisibleForTesting
    static String[] shrink(String... pToShrink) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for(int i1 = 0; i1 < pToShrink.length; ++i1) {
            String s = pToShrink[i1];
            i = Math.min(i, firstNonSpace(s));
            int j1 = lastNonSpace(s);
            j = Math.max(j, j1);
            if (j1 < 0) {
                if (k == i1) {
                    ++k;
                }

                ++l;
            } else {
                l = 0;
            }
        }

        if (pToShrink.length == l) {
            return new String[0];
        } else {
            String[] astring = new String[pToShrink.length - l - k];

            for(int k1 = 0; k1 < astring.length; ++k1) {
                astring[k1] = pToShrink[k1 + k].substring(i, j + 1);
            }

            return astring;
        }
    }

    public boolean isIncomplete() {
        NonNullList<Ingredient> nonnulllist = this.getIngredients();
        return nonnulllist.isEmpty() || nonnulllist.stream().filter((p_151277_) -> {
            return !p_151277_.isEmpty();
        }).anyMatch(ForgeHooks::hasNoElements);
    }

    private static int firstNonSpace(String pEntry) {
        int i;
        for(i = 0; i < pEntry.length() && pEntry.charAt(i) == ' '; ++i) {
        }

        return i;
    }

    private static int lastNonSpace(String pEntry) {
        int i;
        for(i = pEntry.length() - 1; i >= 0 && pEntry.charAt(i) == ' '; --i) {
        }

        return i;
    }

    static String[] patternFromJson(JsonArray pPatternArray) {
        String[] astring = new String[pPatternArray.size()];
        if (astring.length > MAX_HEIGHT) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
        } else if (astring.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for(int i = 0; i < astring.length; ++i) {
                String s = GsonHelper.convertToString(pPatternArray.get(i), "pattern[" + i + "]");
                if (s.length() > MAX_WIDTH) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum");
                }

                if (i > 0 && astring[0].length() != s.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                astring[i] = s;
            }

            return astring;
        }
    }

    static Map<String, Ingredient> keyFromJson(JsonObject pKeyEntry) {
        Map<String, Ingredient> map = Maps.newHashMap();

        for(Map.Entry<String, JsonElement> entry : pKeyEntry.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + (String)entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put(entry.getKey(), Ingredient.fromJson(entry.getValue()));
        }

        map.put(" ", Ingredient.EMPTY);
        return map;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer pContainer) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(pContainer.getContainerSize(), ItemStack.EMPTY);

        for(int i = 0; i < 12; ++i) {
            ItemStack item = pContainer.getItem(i);
            if (item.hasContainerItem()) {
                nonnulllist.set(i, item.getContainerItem());
            }
        }

        return nonnulllist;
    }

    public static ItemStack itemStackFromJson(JsonObject pStackObject) {
        return net.minecraftforge.common.crafting.CraftingHelper.getItemStack(pStackObject, true, true);
    }

    public static Item itemFromJson(JsonObject pItemObject) {
        String s = GsonHelper.getAsString(pItemObject, "item");
        Item item = Registry.ITEM.getOptional(new ResourceLocation(s)).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown item '" + s + "'");
        });
        if (item == Items.AIR) {
            throw new JsonSyntaxException("Invalid item: " + s);
        } else {
            return item;
        }
    }

    @Override
    public int getContainerSize() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return null;
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return null;
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {

    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return false;
    }

    @Override
    public void clearContent() {

    }

    public static class Type implements RecipeType<AssemblyStationRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "assembling";
    }
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>>  implements RecipeSerializer<AssemblyStationRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        private static final ResourceLocation NAME = new ResourceLocation(HighEndTechnology.MOD_ID,"assembling");
        public AssemblyStationRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
            String s = GsonHelper.getAsString(pJson, "group", "");
            Map<String, Ingredient> map = AssemblyStationRecipe.keyFromJson(GsonHelper.getAsJsonObject(pJson, "key"));
            String[] astring = AssemblyStationRecipe.shrink(AssemblyStationRecipe.patternFromJson(GsonHelper.getAsJsonArray(pJson, "pattern")));
            JsonArray assemblyItems = GsonHelper.getAsJsonArray(pJson, "assembly_items");//assembly Items


            int width = astring[0].length();
            int height = astring.length;
            NonNullList<Ingredient> pattern = AssemblyStationRecipe.dissolvePattern(astring, map, width, height);


            //Assembly Pattern
            NonNullList<Ingredient> assemblyIngredients = NonNullList.withSize(assemblyItems.size(), Ingredient.EMPTY);
            int[] count = new int[assemblyItems.size()];
            for (int i = 0; i < assemblyItems.size(); i++) {
                JsonObject object = assemblyItems.get(i).getAsJsonObject();
                if (object.has("tag")){
                    ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(object, "tag"));
                    TagKey<Item> tagKey = TagKey.create(Registry.ITEM_REGISTRY, resourcelocation);
                    Ingredient ingredient = Ingredient.of(tagKey);
                    assemblyIngredients.set(i,ingredient);
                }else {
                    assemblyIngredients.set(i,Ingredient.fromJson(assemblyItems.get(i)));
                }
                if(object.has("count")){
                    count[i] = object.get("count").getAsInt();
                }else {
                    count[i] = 1;
                }
            }
            ItemStack itemstack = AssemblyStationRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "result"));
            return new AssemblyStationRecipe(pRecipeId, s, width, height, pattern, assemblyIngredients, count, itemstack);
        }

        public AssemblyStationRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            int i = pBuffer.readVarInt();
            int j = pBuffer.readVarInt();
            String s = pBuffer.readUtf();
            NonNullList<Ingredient> patternNonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);

            for(int k = 0; k < patternNonnulllist.size(); ++k) {
                patternNonnulllist.set(k, Ingredient.fromNetwork(pBuffer));
            }

            int IngredientCount = pBuffer.readVarInt();
            NonNullList<Ingredient> assemblyNonnulllist = NonNullList.withSize(IngredientCount,Ingredient.EMPTY);
            for (int k = 0; k < assemblyNonnulllist.size(); k++) {
                assemblyNonnulllist.set(k,Ingredient.fromNetwork(pBuffer));
            }
            int length = assemblyNonnulllist.size();
            System.out.println(length);
            int[] count = new int[length];
            for (int k = 0; k < length; k++) {
                count[k] = pBuffer.readVarInt();
            }

            ItemStack itemstack = pBuffer.readItem();
            return new AssemblyStationRecipe(pRecipeId, s, i, j, patternNonnulllist, assemblyNonnulllist, count, itemstack);
        }

        public void toNetwork(FriendlyByteBuf pBuffer, AssemblyStationRecipe pRecipe) {
            pBuffer.writeVarInt(pRecipe.width);
            pBuffer.writeVarInt(pRecipe.height);
            pBuffer.writeUtf(pRecipe.group);

            for(Ingredient ingredient : pRecipe.patternIngredients) {
                ingredient.toNetwork(pBuffer);
            }
            pBuffer.writeVarInt(pRecipe.assemblyIngredients.size());
            for(Ingredient ingredient : pRecipe.assemblyIngredients) {
                ingredient.toNetwork(pBuffer);
            }

            for (int i = 0; i < pRecipe.assemblyCount.length; i++) {
                pBuffer.writeVarInt(pRecipe.assemblyCount[i]);
            }

            pBuffer.writeItem(pRecipe.result);
        }
    }
}
