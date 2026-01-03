package se.meltastudio.cms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "required_part_template")
public class RequiredPartTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_template_id", nullable = false)
    @JsonBackReference(value = "tasktemplate-requiredparttemplate")
    private WorkTaskTemplate taskTemplate;

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    @JsonBackReference(value = "article-requiredparttemplate")
    private Article article;

    @Column(nullable = false)
    private int quantity;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public WorkTaskTemplate getTaskTemplate() { return taskTemplate; }
    public void setTaskTemplate(WorkTaskTemplate taskTemplate) { this.taskTemplate = taskTemplate; }

    public Article getArticle() { return article; }
    public void setArticle(Article article) { this.article = article; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
