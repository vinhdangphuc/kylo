package com.thinkbiganalytics.metadata.modeshape.category;

import com.thinkbiganalytics.metadata.api.category.Category;
import com.thinkbiganalytics.metadata.api.category.CategoryProvider;
import com.thinkbiganalytics.metadata.modeshape.BaseJcrProvider;
import com.thinkbiganalytics.metadata.modeshape.common.EntityUtil;
import com.thinkbiganalytics.metadata.modeshape.common.JcrEntity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A JCR provider for {@link Category} objects.
 */
public class JcrCategoryProvider extends BaseJcrProvider<Category, Category.ID> implements CategoryProvider<Category> {

    @Override
    public Category findBySystemName(String systemName) {

        String query = "SELECT * FROM [" + getNodeType() + "] as cat WHERE cat.[" + JcrCategory.SYSTEM_NAME + "] = '" + systemName + "'";
        return findFirst(query);
    }

    @Override
    public String getNodeType() {
        return JcrCategory.NODE_TYPE;
    }

    @Override
    public Class<? extends Category> getEntityClass() {
        return JcrCategory.class;
    }

    @Override
    public Class<? extends JcrEntity> getJcrEntityClass() {
        return JcrCategory.class;
    }

    @Override
    public Category ensureCategory(String systemName) {
        String path = EntityUtil.pathForCategory();
        Map<String, Object> props = new HashMap<>();
        props.put(JcrCategory.SYSTEM_NAME, systemName);
        return findOrCreateEntity(path, systemName, props);
    }
    /*
    @Override
    public JcrCategory.CategoryId resolveId(Serializable fid) {
        super.resolveId(fid);
        return new JcrCategory.CategoryId(fid);
    }
    */

    @Override
    public Category.ID resolveId(Serializable fid) {
        return new JcrCategory.CategoryId(fid);
    }
}
