package com.zeafen.petwalker.domain.services

import com.zeafen.petwalker.domain.models.api.util.APIResult
import com.zeafen.petwalker.domain.models.api.other.PagedResult
import com.zeafen.petwalker.domain.models.api.pets.Pet
import com.zeafen.petwalker.domain.models.api.pets.PetInfoType
import com.zeafen.petwalker.domain.models.api.pets.PetMedicalInfo
import com.zeafen.petwalker.domain.models.api.pets.PetRequest
import com.zeafen.petwalker.domain.models.PetWalkerFileInfo

interface PetsRepository {
    /***
     * Gets pets of the assignment
     * @return filtered pets list if request succeeded, otherwise - Error info
     * @param assignmentId - Identifier of the assignment
     * @param page - Current page of the pets list
     * @param perPage - Page size of the pets list
     * @param name - Search name of the pets
     * @param species - Search species of the pets
     * @param ageDescending - Age ordering option
     */
    suspend fun getAssignmentPets(
        assignmentId: String,
        page: Int? = null,
        perPage: Int? = null,
        name: String? = null,
        species: String? = null,
        ageDescending: Boolean? = null
    ): APIResult<PagedResult<Pet>, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Gets pets of the current User
     * @return filtered pets list if request succeeded, otherwise - Error info
     * @param page - Current page of the pets list
     * @param perPage - Page size of the pets list
     * @param name - Search name of the pets
     * @param species - Search species of the pets
     * @param ageDescending - Age ordering option
     */
    suspend fun getOwnPets(
        page: Int? = null,
        perPage: Int? = null,
        name: String? = null,
        species: String? = null,
        ageDescending: Boolean? = null
    ): APIResult<PagedResult<Pet>, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Gets pet entity with specified identifier
     * @return Pet entity if request succeeded, otherwise - Error info
     * @param petId - Identifier of the pet
     */
    suspend fun getPet(
        petId: String
    ): APIResult<Pet, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Gets if pet entity with specified identifier id owned by current user
     * @return true if current user owns pet entity, false - if not if request succeeded, otherwise - Error info
     * @param petId - Identifier of the pet
     */
    suspend fun getIfOwnPet(
        petId: String
    ): APIResult<Boolean, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Gets medical info of the pet
     * @return filtered list of the medical info if request succeeded, otherwise - Error info
     * @param page - Current page of the medical info list
     * @param perPage - Page size of the medical info list
     * @param type - Medical info type filtering option
     */
    suspend fun getPetMedicalInfo(
        petId: String,
        type: PetInfoType? = null
    ): APIResult<List<PetMedicalInfo>, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Posts pet data into the source
     * @return posted pet entity if request succeeded, otherwise - Error info
     * @param request - Pet data to post
     */
    suspend fun postPet(
        request: PetRequest
    ): APIResult<Pet, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Posts medical info of the pet into the source
     * @return posted medical info of the pet if request succeeded, otherwise - Error info
     * @param petId - Identifier of the pet
     * @param type - Type of the medical info
     * @param additionalInfo - Additional description about medical information
     * @param document - Document for medical info
     */
    suspend fun postPetMedicalInfo(
        petId: String,
        type: PetInfoType,
        additionalInfo: String? = null,
        document: PetWalkerFileInfo? = null
    ): APIResult<PetMedicalInfo, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Assigns pet to the assignment
     * @param assignmentId - Identifier of the assignment
     * @param petId - Identifier of the pet
     */
    suspend fun postAssignmentPet(
        assignmentId: String,
        petId: String
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Updates pet data
     * @return whenever the request is succeeds or failures
     * @param petId - Identifier of the pet to update
     * @param request - Pet data to post
     */
    suspend fun updatePet(
        petId: String,
        request: PetRequest
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Updates medical info of the pet
     * @return whenever the request is succeeds or failures
     * @param petId - Identifier of the pet
     * @param medicalInfoId - Identifier of the medical info
     * @param type - Type of the medical info
     * @param additionalInfo - Additional description about medical information
     * @param document - Document for medical info
     */
    suspend fun updatePetMedicalInfo(
        petId: String,
        medicalInfoId: String,
        type: PetInfoType,
        additionalInfo: String? = null,
        document: PetWalkerFileInfo? = null
    ): APIResult<PetMedicalInfo, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Deletes pet from the assignment
     * @param assignmentId - Identifier of the assignment
     * @param petId - Identifier of the pet
     */
    suspend fun deleteAssignmentPet(
        assignmentId: String,
        petId: String
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Deletes pet from the source
     * @param petId - Identifier of the pet
     */
    suspend fun deletePet(
        petId: String
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>

    /***
     * Deletes medical info of the pet from the source
     * @param petId - Identifier of the pet
     * @param medicalInfoId - Identifier of the medical info
     */
    suspend fun deletePetMedicalInfo(
        petId: String,
        medicalInfoId: String
    ): APIResult<Unit, com.zeafen.petwalker.domain.models.api.util.Error>
}