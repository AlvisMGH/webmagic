package com.alvism.webmagic.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "wm_prod")
public class ProdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "prod_title", columnDefinition = "varchar(128) default '' comment '商品标题'")
    private String prodTitle;

    @Column(name = "prod_yuan", columnDefinition = "varchar(8) default '0' comment '商品价格（截取元）'")
    private String prodYuan;

    @Column(name = "prod_fen", columnDefinition = "char(2) default '00' comment '商品价格（截取分）'")
    private String prodFen;

    @Column(name = "cover_pic", columnDefinition = "varchar(512) default '' comment '封面图片'")
    private String coverPic;

}
