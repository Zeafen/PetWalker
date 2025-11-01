package com.zeafen.petwalker.presentation.standard.navigation

import com.zeafen.petwalker.presentation.assignments.recruitmentsPage.RecruitmentsLoadGroup
import kotlinx.serialization.Serializable

sealed interface NavigationRoutes {
    //Auth
    @Serializable
    data object AuthNavigation : NavigationRoutes

    @Serializable
    data object SignIn : NavigationRoutes

    @Serializable
    data object SignUp : NavigationRoutes

    @Serializable
    data object Authorise : NavigationRoutes

    @Serializable
    data object ForgotPasswordPage : NavigationRoutes


    //Home
    @Serializable
    data object ProfileNavigation : NavigationRoutes

    @Serializable
    data object HomePage : NavigationRoutes

    @Serializable
    data class RecruitmentsPage(val initialTab: RecruitmentsLoadGroup = RecruitmentsLoadGroup.All) :
        NavigationRoutes

    @Serializable
    data object ProfilePage : NavigationRoutes

    @Serializable
    data class MapPage(val loadInfoJs: String): NavigationRoutes


    //Users
    @Serializable
    data object WalkersNavigation : NavigationRoutes

    @Serializable
    data object WalkersPage : NavigationRoutes

    @Serializable
    data class WalkerInfoPage(val userId: String) : NavigationRoutes

    @Serializable
    data class ReviewConfigurePage(
        val reviewId: String?,
        val assignmentId: String
    ) : NavigationRoutes

    @Serializable
    data class ComplaintConfigurePage(val complaintId: String?, val userId: String) :
        NavigationRoutes


    //Pets navigation
    @Serializable
    data object PetsNavigation : NavigationRoutes

    @Serializable
    data object PetsPage : NavigationRoutes

    @Serializable
    data class PetInfoPage(val petId: String) : NavigationRoutes

    @Serializable
    data class PetConfigurePage(val configurePetId: String?) : NavigationRoutes


    //Assignments
    @Serializable
    data object AssignmentsNavigation : NavigationRoutes

    @Serializable
    data class AssignmentsPage(val loadOwn: Boolean = false) : NavigationRoutes

    @Serializable
    data class AssignmentDetailsPage(val assignmentId: String) : NavigationRoutes

    @Serializable
    data class AssignmentConfigurePage(val configureAssignmentId: String? = null) : NavigationRoutes

    @Serializable
    data class AssignmentChannelPage(val channelAssignmentId: String) : NavigationRoutes

    //Posts
    @Serializable
    data object PostsNavigation : NavigationRoutes

    @Serializable
    data object PostsPage : NavigationRoutes

    @Serializable
    data class PostInfoPage(val postId: String) : NavigationRoutes

    @Serializable
    data class PostConfigurePage(val configurePostId: String?) : NavigationRoutes
}