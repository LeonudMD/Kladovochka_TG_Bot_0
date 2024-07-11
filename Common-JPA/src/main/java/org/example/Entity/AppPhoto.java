package org.example.Entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_photo")
public class AppPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "telegram_file_id")
    private String telegramFileId;

    @OneToOne
    @JoinColumn(name = "binary_content_id")
    private BinaryContent binaryContent;

    @Column(name = "file_size")
    private Integer fileSize;
}
