package se.meltastudio.cms.model;

import jakarta.persistence.*;

@Entity
@Table(name = "work_task_article")
public class WorkTaskArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "work_task_id", nullable = false)
    private WorkTask workTask;

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Column(nullable = false)
    private int quantity;

    // 🛠 Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public WorkTask getWorkTask() { return workTask; }
    public void setWorkTask(WorkTask workTask) { this.workTask = workTask; }

    public Article getArticle() { return article; }
    public void setArticle(Article article) { this.article = article; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
