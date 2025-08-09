package ml.espgm.gesem.helpers

import ml.espgm.gesem.dto.AdminDto
import ml.espgm.gesem.dto.EmployeDto
import ml.espgm.gesem.entities.Admin
import ml.espgm.gesem.entities.Employe

// ---------- ADMIN ----------
fun Admin.toDto(): AdminDto =
    AdminDto(
        id = id,
        fullName = fullName,
        username = username,
        email = email,
        password = password,
        role = role,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

fun AdminDto.toEntity(): Admin =
    Admin(
        id = id,
        fullName = fullName,
        username = username,
        email = email,
        password = password,
        role = role,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

// ---------- EMPLOYE ----------
fun Employe.toDto(): EmployeDto =
    EmployeDto(
        id = id,
        lastName = lastName,
        firstName = firstName,
        poste = poste,
        email = email,
        hiringDate = hiringDate,
        enabled = enabled,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

fun EmployeDto.toEntity(): Employe =
    Employe(
        id = id,
        lastName = lastName,
        firstName = firstName,
        poste = poste,
        email = email,
        hiringDate = hiringDate,
        enabled = enabled,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
