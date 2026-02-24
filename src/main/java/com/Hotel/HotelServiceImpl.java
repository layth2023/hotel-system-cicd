package com.Hotel;

import com.Amenity.Amenity;
import com.Amenity.AmenityMapper;
import com.Amenity.AmenityNotFoundException;
import com.Amenity.AmenityRepository;
import com.Amenity.AmenityResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;
    private final AmenityRepository amenityRepository;
    private final AmenityMapper amenityMapper;

    public HotelServiceImpl(HotelRepository hotelRepository,
                            HotelMapper hotelMapper,
                            AmenityRepository amenityRepository,
                            AmenityMapper amenityMapper) {
        this.hotelRepository = hotelRepository;
        this.hotelMapper = hotelMapper;
        this.amenityRepository = amenityRepository;
        this.amenityMapper = amenityMapper;
    }

    @Override
    public HotelResponseDTO create(HotelRequestDTO requestDTO) {

        String normalizedName = normalize(requestDTO.getName());

        if (hotelRepository.existsByNameInsensitive(normalizedName)) {
            throw new HotelAlreadyExistsException(normalizedName);
        }

        requestDTO.setName(normalizedName);

        Hotel hotel = hotelMapper.toEntity(requestDTO);
        Hotel saved = hotelRepository.save(hotel);

        return hotelMapper.toResponseDTO(saved);
    }

    @Override
    public HotelResponseDTO getById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
        return hotelMapper.toResponseDTO(hotel);
    }

    @Override
    public Page<HotelResponseDTO> getAll(Pageable pageable) {
        return hotelRepository.findAll(pageable)
                .map(hotelMapper::toResponseDTO);
    }

    @Override
    public HotelResponseDTO update(Long id, HotelRequestDTO requestDTO) {

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));

        String normalizedName = normalize(requestDTO.getName());

        if (hotelRepository.existsByNameInsensitiveAndIdNot(normalizedName, id)) {
            throw new HotelAlreadyExistsException(normalizedName);
        }

        requestDTO.setName(normalizedName);

        hotelMapper.updateEntity(hotel, requestDTO);
        Hotel saved = hotelRepository.save(hotel);

        return hotelMapper.toResponseDTO(saved);
    }

    @Override
    public void delete(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
        hotelRepository.delete(hotel);
    }

    @Override
    public void addAmenity(Long hotelId, Long amenityId) {

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId));

        Amenity amenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> new AmenityNotFoundException(amenityId));

        hotel.getAmenities().add(amenity);
        amenity.getHotels().add(hotel);
    }

    @Override
    public void removeAmenity(Long hotelId, Long amenityId) {

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId));

        Amenity amenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> new AmenityNotFoundException(amenityId));

        hotel.getAmenities().remove(amenity);
        amenity.getHotels().remove(hotel);
    }

    @Override
    public Page<AmenityResponseDTO> getHotelAmenitiesActive(Long hotelId, Pageable pageable) {

        ensureHotelExists(hotelId);

        return amenityRepository
                .findByHotels_IdAndIsActiveTrue(hotelId, pageable)
                .map(amenityMapper::toResponseDTO);
    }

    @Override
    public Page<AmenityResponseDTO> getHotelAmenitiesInactive(Long hotelId, Pageable pageable) {

        ensureHotelExists(hotelId);

        return amenityRepository
                .findByHotels_IdAndIsActiveFalse(hotelId, pageable)
                .map(amenityMapper::toResponseDTO);
    }

    @Override
    public Page<AmenityResponseDTO> getHotelAmenitiesAll(Long hotelId, Pageable pageable) {

        ensureHotelExists(hotelId);

        return amenityRepository
                .findByHotels_Id(hotelId, pageable)
                .map(amenityMapper::toResponseDTO);
    }

    private void ensureHotelExists(Long hotelId) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new HotelNotFoundException(hotelId);
        }
    }

    private String normalize(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Hotel name cannot be null");
        }
        return name.trim();
    }
}