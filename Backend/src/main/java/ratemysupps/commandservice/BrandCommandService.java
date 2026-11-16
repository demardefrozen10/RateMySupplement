package ratemysupps.commandservice;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ratemysupps.entity.Brand;
import ratemysupps.icommandservice.IBrandCommandService;
import ratemysupps.repository.IBrandRepository;
import ratemysupps.repository.IReviewRepository;
import ratemysupps.mapper.ReadBrandMapper;
import ratemysupps.mapper.WriteBrandMapper;
import ratemysupps.readmodel.ReadBrand;
import ratemysupps.writemodel.WriteBrand;

@Service
public class BrandCommandService implements IBrandCommandService {

    private final WriteBrandMapper writeMapper;
    private final ReadBrandMapper readMapper;
    private final IBrandRepository repo;
    private final IReviewRepository reviewRepo;
    public BrandCommandService(WriteBrandMapper writeMapper, IBrandRepository repo, ReadBrandMapper readMapper, IReviewRepository reviewRepo) {
        this.writeMapper = writeMapper;
        this.repo = repo;
        this.readMapper = readMapper;
        this.reviewRepo = reviewRepo;
    }

    @Override
    public ReadBrand submitBrand(WriteBrand brand) {
        return readMapper.fromEntity(repo.save(writeMapper.toEntity(brand)));
    }

    @Override
    @Transactional
    public ReadBrand approveBrand(Long brandId) {
        Brand brand = repo.findById(brandId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Brand not found"
        ));

        brand.setVerified(true);
        return readMapper.fromEntity(brand);
    }

    @Override
    @Transactional
    public void rejectBrand(Long brandId) {
        Brand brand = repo.findById(brandId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Brand not found"
        ));

        if (brand.isVerified()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot reject an approved brand");
        }

        if (brand.getSupplements() != null) {
            brand.getSupplements().forEach(s -> reviewRepo.deleteAll(reviewRepo.findBySupplementId(s.getId())));
            reviewRepo.flush();
        }
        repo.delete(brand);
    }
}
