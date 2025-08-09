package ml.espgm.gesem.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import ml.espgm.gesem.annotations.HandleException
import ml.espgm.gesem.dto.EmployeDto
import ml.espgm.gesem.services.EmployeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema

@RestController
@RequestMapping("/api/v1/employes")
class EmployeCtrl(
    private val employeService: EmployeService
) {

    @HandleException
    @PostMapping
    @Operation(summary = "Créer un employé", description = "Ajoute un nouvel employé à la base de données")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Employé créé avec succès"),
        ApiResponse(responseCode = "400", description = "Requête invalide", content = [Content(schema = Schema(hidden = true))])
    ])
    fun create(@RequestBody employeDto: EmployeDto): Any {
        val created = employeService.create(employeDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(created)
    }

    @HandleException
    @GetMapping
    @Operation(summary = "Lister les employés", description = "Récupère la liste de tous les employés")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    fun getAll(): Any {
        val employes = employeService.getAll()
        return ResponseEntity.ok(employes)
    }

    @HandleException
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un employé", description = "Récupère un employé via son identifiant")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Employé trouvé"),
        ApiResponse(responseCode = "404", description = "Employé non trouvé", content = [Content(schema = Schema(hidden = true))])
    ])
    fun getById(@PathVariable id: Long): Any {
        val employe = employeService.getById(id)
        return ResponseEntity.ok(employe)
    }

    @HandleException
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un employé", description = "Met à jour les informations d’un employé existant")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Employé mis à jour"),
        ApiResponse(responseCode = "404", description = "Employé non trouvé", content = [Content(schema = Schema(hidden = true))])
    ])
    fun update(
        @PathVariable id: Long,
        @RequestBody employeDto: EmployeDto
    ): Any {
        val updated = employeService.update(id, employeDto)
        return ResponseEntity.ok(updated)
    }

    @HandleException
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un employé", description = "Supprime un employé via son identifiant")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Employé supprimé"),
        ApiResponse(responseCode = "404", description = "Employé non trouvé", content = [Content(schema = Schema(hidden = true))])
    ])
    fun delete(@PathVariable id: Long): Any {
        return employeService.delete(id)
    }
}