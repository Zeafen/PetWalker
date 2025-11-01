package com.zeafen.petwalker.data.helpers

import androidx.annotation.IntRange
import com.zeafen.petwalker.domain.models.ValidationInfo
import petwalker.composeapp.generated.resources.Res
import petwalker.composeapp.generated.resources.incorrect_length_least_error
import petwalker.composeapp.generated.resources.must_contain_least_letters_error
import petwalker.composeapp.generated.resources.must_contain_least_numbers_error
import petwalker.composeapp.generated.resources.must_contain_lowercase
import petwalker.composeapp.generated.resources.must_contain_specials_error
import petwalker.composeapp.generated.resources.must_contain_uppercase

fun String.isValidEmail(): Boolean {
    return this.matches(Regex("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", RegexOption.IGNORE_CASE))
}

fun String.isValidPhoneNumber(): Boolean {
    return this.matches(Regex("^\\+?\\d{1,4}[-.\\s]?\\(?\\d{1,3}?\\(?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]\\d{4,9}"))
}

fun String.isValidPassword(
    @IntRange(1, Long.MAX_VALUE)
    minLength: Int = 15,
    @IntRange(0, Long.MAX_VALUE)
    lettersLeastCount: Int = 1,
    @IntRange(0, Long.MAX_VALUE)
    numbersLeastCount: Int = 1,
    hasSpecials: Boolean = true,
    hasUpperCase: Boolean = true,
    hasLowerCase: Boolean = true
): ValidationInfo {
    return when {
        this.length < minLength -> ValidationInfo(
            false, Res.string.incorrect_length_least_error, listOf(
                minLength
            )
        )

        this.count { it.isDigit() } < numbersLeastCount -> ValidationInfo(
            false, Res.string.must_contain_least_numbers_error, listOf(
                numbersLeastCount
            )
        )

        this.count { it.isLetter() } < lettersLeastCount -> ValidationInfo(
            false, Res.string.must_contain_least_letters_error, listOf(
                lettersLeastCount
            )
        )

        !this.contains("[!\"#\$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~]".toRegex()) == hasSpecials -> ValidationInfo(
            false, Res.string.must_contain_specials_error, emptyList()
        )

        !this.contains("[A-Z]".toRegex()) == hasUpperCase -> ValidationInfo(
            false, Res.string.must_contain_uppercase, emptyList()
        )

        !this.contains("[a-z]".toRegex()) == hasLowerCase -> ValidationInfo(
            false, Res.string.must_contain_lowercase, emptyList()
        )

        else -> ValidationInfo(true, null, emptyList())
    }
}

fun String.containsAny(
    ignoreCase: Boolean,
     vararg others: CharSequence,
): Boolean {
    return others.any {
        this.contains(it, ignoreCase)
    }
}

fun String.countWords(): Int {
    return this.split(" ").count()
}