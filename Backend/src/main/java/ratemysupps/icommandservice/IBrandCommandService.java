package ratemysupps.icommandservice;

import ratemysupps.readmodel.ReadBrand;
import ratemysupps.writemodel.WriteBrand;

public interface IBrandCommandService {

    public ReadBrand submitBrand(WriteBrand brand);

    public ReadBrand approveBrand(Long brandId);

}
