package ml.espgm.gesem.services

import ml.espgm.gesem.dto.EmployeDto

interface EmployeService {

    fun create(employe: EmployeDto): EmployeDto
    fun getAll(): List<EmployeDto>
    fun getById(id: Long): EmployeDto
    fun update(id: Long, employe: EmployeDto): EmployeDto
    fun delete(id: Long): String

}