package org.pra.nse.csv.transform;

import org.pra.nse.ApCo;
import org.pra.nse.util.NseFileUtils;
import org.pra.nse.util.PraFileUtils;

import java.io.File;


public class BaseTransformer {
    protected final String Data_Dir = ApCo.BASE_DATA_DIR + File.separator + ApCo.CM_DIR_NAME;

    protected final TransformHelper transformHelper;
    protected final NseFileUtils nseFileUtils;
    protected final PraFileUtils praFileUtils;

    public BaseTransformer(TransformHelper transformHelper, NseFileUtils nseFileUtils, PraFileUtils praFileUtils) {
        this.transformHelper = transformHelper;
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
    }

}
