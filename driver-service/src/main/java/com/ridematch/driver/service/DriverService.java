package com.ridematch.driver.service;

import com.ridematch.driver.dto.*;
import com.ridematch.driver.entity.Driver;
import com.ridematch.driver.entity.Vehicle;
import com.ridematch.driver.excpetion.UploadException;
import com.ridematch.driver.mapper.DriverMapper;
import com.ridematch.driver.mapper.VehicleMapper;
import com.ridematch.driver.repository.DriverRepository;
import com.ridematch.driver.repository.VehicleRepository;
import com.ridematch.driver.util.FileFormatter;
import com.ridematch.driver.Validation.FileValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.ridematch.driver.enums.ErrorCode.FILE_UPLOAD_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverService {
    private final MinioStorageService minioStorageService;
    private final VehicleMapper vehicleMapper;
    private final DriverMapper driverMapper;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final FileValidator fileValidator;

    @Transactional
    public void register(
            MultipartFile licenseDocument,
            MultipartFile registrationDocument,
            List<MultipartFile> images,
            RegisterRequest request) {

        fileValidator.validate(true, licenseDocument, registrationDocument, images);

        Vehicle vehicle = vehicleMapper.mapEntityToEntity(request.getVehicleInformationDTO());
        vehicle = vehicleRepository.save(vehicle);

        Driver driver = driverMapper.toEntity(request, vehicle);
        driver = driverRepository.save(driver);

        storeDriverAndVehicleDocuments(
                driver, vehicle, licenseDocument, registrationDocument, images);
    }

    public DriverDetailsResponse viewDriverDetails(Long id) { // TODO : Use mapper
        driverRepository.findById(id);
        Driver driver = driverRepository.findById(id).get();
        DriverDetailsResponse driverDetailsResponse = new DriverDetailsResponse();
        driverDetailsResponse.setId(id);
        driverDetailsResponse.setEmail(driver.getEmail());
        driverDetailsResponse.setFullName(driver.getFullName());
        return minioStorageService.populateDriverFiles(id, driverDetailsResponse);
    }

    // TODO: Get id from token
    public void updateDocuments(
            Long id,
            MultipartFile licenseDocument,
            MultipartFile registrationDocument,
            List<MultipartFile> images) {
        Driver driver = driverRepository.findById(id).get();
        fileValidator.validate(false, licenseDocument, registrationDocument, images);

        storeDriverAndVehicleDocuments(
                driver, driver.getVehicle(), licenseDocument, registrationDocument, images);
    }

    public PaginatedResponse<DriverResponse> viewAllDrivers(
            Specification<Driver> driverFilter, Pageable pageable) {
        Page<Driver> driversPage = driverRepository.findAll(driverFilter, pageable);

        return new PaginatedResponse<>(
                driverMapper.toDriverResponse(driversPage.getContent()),
                driversPage.getNumber(),
                driversPage.getSize(),
                driversPage.getTotalElements(),
                driversPage.getTotalPages());
    }

    public void updateDriverStatus(Long id, StatusUpdateRequest statusUpdateRequest) {
        driverRepository.findById(id).get().setStatus(statusUpdateRequest.getDriverStatus());
    }

    private void storeDriverAndVehicleDocuments(
            Driver driver,
            Vehicle vehicle,
            MultipartFile licenseDocument,
            MultipartFile registrationDocument,
            List<MultipartFile> images) {

        List<String> uploadedKeys = new ArrayList<>();
        try {
            if (licenseDocument != null) {
                String licenseKey =
                        FileFormatter.license(
                                driver.getId(), licenseDocument.getOriginalFilename());
                minioStorageService.put(licenseKey, licenseDocument);
                uploadedKeys.add(licenseKey);
                driver.setLicenseDocument(licenseKey);
            }

            if (registrationDocument != null) {
                String regKey =
                        FileFormatter.registration(
                                driver.getId(), registrationDocument.getOriginalFilename());
                minioStorageService.put(regKey, registrationDocument);
                uploadedKeys.add(regKey);
                vehicle.setRegistrationDocument(regKey);
            }

            if (images != null) {
                for (int i = 0; i < images.size(); i++) {
                    MultipartFile img = images.get(i);
                    String imgKey =
                            FileFormatter.image(driver.getId(), i, img.getOriginalFilename());
                    minioStorageService.put(imgKey, img);
                    uploadedKeys.add(imgKey);
                }
            }

            vehicle.setImages(uploadedKeys.stream().filter(k -> k.contains("/images/")).toList());

        } catch (Exception e) {
            log.error("Failed to upload files for driverId={}", driver.getId(), e);
            throw new UploadException(FILE_UPLOAD_ERROR);
        }
    }
}
