package ml.espgm.gesem.controllers

import com.fasterxml.jackson.annotation.JsonView
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
import ml.espgm.gesem.views.CreationViews

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
    @JsonView(CreationViews.Get::class)
    fun create(@RequestBody @JsonView(CreationViews.Creation::class) employeDto: EmployeDto): Any {
        return employeService.create(employeDto)
    }

    @HandleException
    @GetMapping
    @Operation(summary = "Lister les employés", description = "Récupère la liste de tous les employés")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @JsonView(CreationViews.Get::class)
    fun getAll(): Any {
        return employeService.getAll()
    }

    @HandleException
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un employé", description = "Récupère un employé via son identifiant")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Employé trouvé"),
        ApiResponse(responseCode = "404", description = "Employé non trouvé", content = [Content(schema = Schema(hidden = true))])
    ])
    @JsonView(CreationViews.Get::class)
    fun getById(@PathVariable id: Long): Any {
        return employeService.getById(id)
    }

    @HandleException
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un employé", description = "Met à jour les informations d’un employé existant")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Employé mis à jour"),
        ApiResponse(responseCode = "404", description = "Employé non trouvé", content = [Content(schema = Schema(hidden = true))])
    ])
    @JsonView(CreationViews.Creation::class)
    fun update(
        @PathVariable id: Long,
        @RequestBody @JsonView(CreationViews.Creation::class) employeDto: EmployeDto
    ): Any {
        return employeService.update(id, employeDto)
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