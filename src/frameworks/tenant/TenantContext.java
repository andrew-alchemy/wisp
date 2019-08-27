package frameworks.tenant;

import java.io.Serializable;
import java.util.Objects;

/**
 * Tenant Identifier
 * 
 * @author Frameworks Inc.
 */
public class TenantContext implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String cd;
	
	public TenantContext(String cd) {
		this.cd = cd;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this == obj || (
				obj instanceof TenantContext
				&& Objects.equals(this.cd, ((TenantContext)obj).cd));
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.cd);
	}
	
	@Override
	public String toString() {
		return "TenantContext[cd=" + this.cd + "]";
	}
	
	/* getters and setters */

	public String getCd() {
		return cd;
	}
}
