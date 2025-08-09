package ml.espgm.gesem.services.impls

import ml.espgm.gesem.dto.EmployeDto
import ml.espgm.gesem.helpers.toDto
import ml.espgm.gesem.helpers.toEntity
import ml.espgm.gesem.repositories.EmployeRepo
import ml.espgm.gesem.services.EmployeService
import org.springframework.stereotype.Service

@Service
class EmployeImpl(
    private val repo: EmployeRepo
): EmployeService {

    override fun create(employe: EmployeDto): EmployeDto {
        val entity = employe.toEntity()
        val saved = repo.save(entity)
        return saved.toDto()
    }

    override fun getAll(): List<EmployeDto> {
        return repo.findAll().map { it.toDto() }
    }

    override fun getById(id: Long): EmployeDto {
        val entity = repo.findById(id)
            .orElseThrow { NoSuchElementException("Employé avec id=$id introuvable") }
        return entity.toDto()
    }

    override fun update(id: Long, employe: EmployeDto): EmployeDto {
        val existing = repo.findById(id)
            .orElseThrow { NoSuchElementException("Employé avec id=$id introuvable") }

        val updated = existing.copy(
            lastName = employe.lastName,
            firstName = employe.firstName,
            poste = employe.poste,
            email = employe.email,
            hiringDate = employe.hiringDate,
            enabled = employe.enabled
        )

        val saved = repo.save(updated)
        return saved.toDto()
    }

    override fun delete(id: Long): String {
        if (!repo.existsById(id)) {
            throw NoSuchElementException("Employé avec id=$id introuvable")
        }
        repo.deleteById(id)
        return "Suppression reussi"
    }
}