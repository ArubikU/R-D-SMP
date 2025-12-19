package net.rollanddeath.smp.core.scripting;

public record ActionResult(boolean deny) {
    public static final ActionResult ALLOW = new ActionResult(false);
    public static final ActionResult DENY = new ActionResult(true);
}
