package java.com.arcbees.plugin.idea.utils;

import com.intellij.openapi.module.Module;

public class PackageIndex {
    private Module module;
    private String name;

    public PackageIndex() {}

    public PackageIndex(Module module, String name) {
        this.module = module;
        this.name = name;
    }

    public Module getModule() {
        return module;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PackageIndex that = (PackageIndex) o;

        if (module != null ? !module.equals(that.module) : that.module != null) return false;
        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        int result = module != null ? module.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
