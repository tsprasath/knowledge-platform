package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Competency {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private String competencyCode;
    private String subject;
    private String competencyLabel;
    private String competencyDescription;
    private String competencyLevel1Label;
    private String competencyLevel1Description;
    private String competencyLevel2Label;
    private String competencyLevel2Description;
    private String competencyLevel3Label;
    private String competencyLevel3Description;
    private String competencyLevel4Label;
    private String competencyLevel4Description;
    private String competencyLevel5Label;
    private String competencyLevel5Description;

    public String getCompetencyCode() {
        return competencyCode;
    }

    public void setCompetencyCode(String competencyCode) {
        this.competencyCode = competencyCode;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCompetencyLabel() {
        return competencyLabel;
    }

    public void setCompetencyLabel(String competencyLabel) {
        this.competencyLabel = competencyLabel;
    }

    public String getCompetencyDescription() {
        return competencyDescription;
    }

    public void setCompetencyDescription(String competencyDescription) {
        this.competencyDescription = competencyDescription;
    }

    public String getCompetencyLevel1Label() {
        return competencyLevel1Label;
    }

    public void setCompetencyLevel1Label(String competencyLevel1Label) {
        this.competencyLevel1Label = competencyLevel1Label;
    }

    public String getCompetencyLevel1Description() {
        return competencyLevel1Description;
    }

    public void setCompetencyLevel1Description(String competencyLevel1Description) {
        this.competencyLevel1Description = competencyLevel1Description;
    }

    public String getCompetencyLevel2Label() {
        return competencyLevel2Label;
    }

    public void setCompetencyLevel2Label(String competencyLevel2Label) {
        this.competencyLevel2Label = competencyLevel2Label;
    }

    public String getCompetencyLevel2Description() {
        return competencyLevel2Description;
    }

    public void setCompetencyLevel2Description(String competencyLevel2Description) {
        this.competencyLevel2Description = competencyLevel2Description;
    }

    public String getCompetencyLevel3Label() {
        return competencyLevel3Label;
    }

    public void setCompetencyLevel3Label(String competencyLevel3Label) {
        this.competencyLevel3Label = competencyLevel3Label;
    }

    public String getCompetencyLevel3Description() {
        return competencyLevel3Description;
    }

    public void setCompetencyLevel3Description(String competencyLevel3Description) {
        this.competencyLevel3Description = competencyLevel3Description;
    }

    public String getCompetencyLevel4Label() {
        return competencyLevel4Label;
    }

    public void setCompetencyLevel4Label(String competencyLevel4Label) {
        this.competencyLevel4Label = competencyLevel4Label;
    }

    public String getCompetencyLevel4Description() {
        return competencyLevel4Description;
    }

    public void setCompetencyLevel4Description(String competencyLevel4Description) {
        this.competencyLevel4Description = competencyLevel4Description;
    }

    public String getCompetencyLevel5Label() {
        return competencyLevel5Label;
    }

    public void setCompetencyLevel5Label(String competencyLevel5Label) {
        this.competencyLevel5Label = competencyLevel5Label;
    }

    public String getCompetencyLevel5Description() {
        return competencyLevel5Description;
    }

    public void setCompetencyLevel5Description(String competencyLevel5Description) {
        this.competencyLevel5Description = competencyLevel5Description;
    }
}
