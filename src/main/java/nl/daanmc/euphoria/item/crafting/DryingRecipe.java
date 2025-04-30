package nl.daanmc.euphoria.item.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;

public class DryingRecipe {
    @Nonnull
    private final ItemStack input;
    private final ItemStack output;
    private final int dryingTime;

    public DryingRecipe(@Nonnull ItemStack input, @Nonnull ItemStack output, int dryingTime) {
        this.input = input;
        this.output = output;
        this.dryingTime = dryingTime;
    }

    public boolean isInput(@Nonnull ItemStack stack) {
        return OreDictionary.itemMatches(this.getInput(), stack, true);
    }

    @Nonnull
    public ItemStack getOutput(@Nonnull ItemStack input) {
        return isInput(input) ? getOutput().copy() : ItemStack.EMPTY;
    }

    @Nonnull
    public ItemStack getInput() {
        return input;
    }

    @Nonnull
    public ItemStack getOutput() {
        return output;
    }

    public int getDryingTime() {
        return dryingTime;
    }
}