package ml.espgm.gesem.repositories

import ml.espgm.gesem.entities.Employe
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmployeRepo: JpaRepository<Employe, Long> {}