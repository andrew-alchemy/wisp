package frameworks.tenant;

/**
 * Tenant Context Holder for tracking TenantContext in a ThreadLocal.
 * 
 * @author Frameworks Inc.
 */
public abstract class TenantContextHolder {

	private static final ThreadLocal<TenantContext> contextHolder = new ThreadLocal<TenantContext>();

    public static TenantContext getContext() {
        return contextHolder.get();
    }
    
    public static void clearContext() {
        contextHolder.remove();
    }

    public static void setContext(TenantContext context) {
        contextHolder.set(context);
    }

}
