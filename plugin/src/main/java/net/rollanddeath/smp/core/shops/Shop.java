package net.rollanddeath.smp.core.shops;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Shop {

    private final UUID owner;
    private final Location signLocation;
    private final Location containerLocation;
    private final ItemDescriptor sellItem;
    private final int sellAmount;
    private final ItemDescriptor priceItem;
    private final int priceAmount;
    private final List<ItemStack> wallet = new ArrayList<>();

    public Shop(UUID owner, Location signLocation, Location containerLocation, ItemDescriptor sellItem, int sellAmount, ItemDescriptor priceItem, int priceAmount) {
        this.owner = owner;
        this.signLocation = signLocation;
        this.containerLocation = containerLocation;
        this.sellItem = sellItem;
        this.sellAmount = sellAmount;
        this.priceItem = priceItem;
        this.priceAmount = priceAmount;
    }

    public UUID getOwner() {
        return owner;
    }

    public Location getSignLocation() {
        return signLocation;
    }

    public Location getContainerLocation() {
        return containerLocation;
    }

    public ItemDescriptor getSellItem() {
        return sellItem;
    }

    public int getSellAmount() {
        return sellAmount;
    }

    public ItemDescriptor getPriceItem() {
        return priceItem;
    }

    public int getPriceAmount() {
        return priceAmount;
    }

    public List<ItemStack> getWallet() {
        return Collections.unmodifiableList(wallet);
    }

    public void addPayment(ItemStack stack) {
        if (stack == null) return;
        wallet.add(stack.clone());
    }

    public void addPayments(List<ItemStack> stacks) {
        if (stacks == null) return;
        stacks.forEach(this::addPayment);
    }

    public void clearWallet() {
        wallet.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shop shop)) return false;
        return Objects.equals(signLocation, shop.signLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(signLocation);
    }
}
