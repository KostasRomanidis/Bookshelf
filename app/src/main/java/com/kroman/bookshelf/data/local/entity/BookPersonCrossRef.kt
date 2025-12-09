package com.kroman.bookshelf.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "book_person_cross_ref",
    primaryKeys = ["bookId", "personId", "type"],
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PersonEntity::class,
            parentColumns = ["personId"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bookId"), Index("personId")]
)
data class BookPersonCrossRef(
    val bookId: Int,
    val personId: Long,
    val type: PersonType
)

data class BookWithRelations(
    @Embedded val book: BookEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "personId",
        associateBy = Junction(
            value = BookPersonCrossRef::class,
            parentColumn = "bookId",
            entityColumn = "personId"
        )
    )
    val authors: List<PersonEntity>,
    val translators: List<PersonEntity>
)

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey val bookId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)