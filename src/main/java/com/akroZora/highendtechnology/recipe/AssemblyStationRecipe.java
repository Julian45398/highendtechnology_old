package com.akroZora.highendtechnology.recipe;

import com.akroZora.highendtechnology.HighEndTechnology;
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
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class AssemblyStationRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;

    private static final int MAX_HEIGHT = 3;
    private static final int MAX_WIDTH = 3;

    public AssemblyStationRecipe(ResourceLocation id, ItemStack output,
                                   NonNullList<Ingredient> recipeItems) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.recipeItems;
    }

    public final int getIngredientCount(){
        return this.recipeItems.size();
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
       return matchesAssemblyItems(pContainer);
    }
    public boolean matchesAssemblyItems(SimpleContainer pContainer){
        for (int i=0;i<recipeItems.size();i++) {
            boolean match=false;
            if(i<9){
                if(recipeItems.get(i).test(pContainer.getItem(i))){
                    match = true;
                }
            }else {
                for (int j = 9; j < pContainer.getContainerSize(); j++) {
                    if(recipeItems.get(i).isEmpty()){
                        if(pContainer.getItem(j).isEmpty()){
                            match=true;
                            break;//the other slots are Empty
                        }
                    }
                    if(recipeItems.get(i).test(pContainer.getItem(j))){
                        System.out.println("test Matches");
                        if(recipeItems.get(i).getItems()[0].getCount() <= pContainer.getItem(j).getCount()){
                            System.out.println("count ");
                            match=true;
                            break; //has ingredient necessary
                        }
                    }
                }
            }
            if (!match){
                return false; //doesn´t have the necessary Ingredient
            }
        }
        System.out.println("hasMatch");
        return true; //has all ingredients
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<AssemblyStationRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "assembling";
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

    public static class Serializer implements RecipeSerializer<AssemblyStationRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(HighEndTechnology.MOD_ID,"assembling");

        @Override
        public AssemblyStationRecipe fromJson(ResourceLocation id, JsonObject json) {
            ItemStack output = AssemblyStationRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

            JsonArray jsonPattern = GsonHelper.getAsJsonArray(json, "pattern"); //pattern
            JsonArray assemblyItems = GsonHelper.getAsJsonArray(json, "assembly_items");//assembly Items
            JsonObject key = GsonHelper.getAsJsonObject(json,"key");
            //gridPattern
            NonNullList<Ingredient> pattern=dissolvePattern(patternFromJson(jsonPattern),keyFromJson(key),3,3);
            //Assembly Pattern
            NonNullList<Ingredient> inputs = NonNullList.withSize(12, Ingredient.EMPTY);
            for (int i = 0; i < inputs.size(); i++) {
                if(i<9){
                    inputs.set(i,pattern.get(i));
                } else if (i<assemblyItems.size()+9) {
                    inputs.set(i,Ingredient.of(AssemblyStationRecipe.itemStackFromJson(assemblyItems.get(i-9).getAsJsonObject())));
                } else if(assemblyItems.size()+9<=i){
                    inputs.set(i,Ingredient.of(ItemStack.EMPTY));
                }
            }
            return new AssemblyStationRecipe(id, output, inputs);
        }

        @Override
        public AssemblyStationRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }

            ItemStack output = buf.readItem();
            return new AssemblyStationRecipe(id, output, inputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, AssemblyStationRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }
            buf.writeItemStack(recipe.getResultItem(), false);
        }

        @Override
        public RecipeSerializer<?> setRegistryName(ResourceLocation name) {
            return INSTANCE;
        }

        @Nullable
        @Override
        public ResourceLocation getRegistryName() {
            return ID;
        }

        @Override
        public Class<RecipeSerializer<?>> getRegistryType() {
            return Serializer.castClass(RecipeSerializer.class);
        }

        @SuppressWarnings("unchecked") // Need this wrapper, because generics
        private static <G> Class<G> castClass(Class<?> cls) {
            return (Class<G>)cls;
        }
    }
}
