package repository

import models.Competency
import org.springframework.data.domain.{Example, Page, Pageable, Sort}
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

import java.util.Optional
import java.{lang, util}
@Repository
trait CompetencyRepositoryTrait extends JpaRepository[Competency, String] {
}