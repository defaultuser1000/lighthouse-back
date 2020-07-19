package ru.zakrzhevskiy.lighthouse.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.zakrzhevskiy.lighthouse.model.reference_gallery.ReferencePhoto;

public interface ReferencePhotosRepository extends JpaRepository<ReferencePhoto, Long> {
    Page<ReferencePhoto> findByUserId(Long id, Pageable pageable);
}
