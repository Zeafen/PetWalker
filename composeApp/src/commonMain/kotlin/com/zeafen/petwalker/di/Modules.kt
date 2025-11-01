package com.zeafen.petwalker.di

import com.zeafen.petwalker.data.services.api.PetWalkerAssignmentsRepository
import com.zeafen.petwalker.data.services.api.PetWalkerAuthRepository
import com.zeafen.petwalker.data.services.api.PetWalkerChannelsRepository
import com.zeafen.petwalker.data.services.api.PetWalkerPetsRepository
import com.zeafen.petwalker.data.services.api.PetWalkerPostsRepository
import com.zeafen.petwalker.data.services.api.PetWalkerProfileRepository
import com.zeafen.petwalker.data.services.api.PetWalkerRecruitmentsRepository
import com.zeafen.petwalker.data.services.api.PetWalkerReviewsRepository
import com.zeafen.petwalker.data.services.api.PetWalkerUsersRepository
import com.zeafen.petwalker.domain.services.AssignmentsRepository
import com.zeafen.petwalker.domain.services.AuthRepository
import com.zeafen.petwalker.domain.services.ChannelsRepository
import com.zeafen.petwalker.domain.services.PetsRepository
import com.zeafen.petwalker.domain.services.PostsRepository
import com.zeafen.petwalker.domain.services.ProfileRepository
import com.zeafen.petwalker.domain.services.RecruitmentsRepository
import com.zeafen.petwalker.domain.services.ReviewsRepository
import com.zeafen.petwalker.domain.services.UsersRepository
import com.zeafen.petwalker.presentation.assignments.assignmentConfigure.AssignmentConfigureViewModel
import com.zeafen.petwalker.presentation.assignments.assignmentDetailsPage.AssignmentDetailsViewModel
import com.zeafen.petwalker.presentation.assignments.assignmentsList.AssignmentsViewModel
import com.zeafen.petwalker.presentation.assignments.recruitmentsPage.RecruitmentsPageViewModel
import com.zeafen.petwalker.presentation.auth.AuthViewModel
import com.zeafen.petwalker.presentation.auth.ForgotPasswordViewModel
import com.zeafen.petwalker.presentation.channel.ChannelDetailsViewModel
import com.zeafen.petwalker.presentation.home.HomePageViewModel
import com.zeafen.petwalker.presentation.map.MapScreenViewModel
import com.zeafen.petwalker.presentation.pets.petConfigure.PetConfigureViewModel
import com.zeafen.petwalker.presentation.pets.petDetails.PetDetailsViewModel
import com.zeafen.petwalker.presentation.pets.petsList.PetsPageViewModel
import com.zeafen.petwalker.presentation.posts.postConfigure.PostConfigureViewModel
import com.zeafen.petwalker.presentation.posts.postDetailsPage.PostDetailsViewModel
import com.zeafen.petwalker.presentation.posts.postsList.PostsPageViewModel
import com.zeafen.petwalker.presentation.profile.ProfilePageViewModel
import com.zeafen.petwalker.presentation.reviews.complaintConfigure.ComplaintConfigureViewModel
import com.zeafen.petwalker.presentation.reviews.reviewConfigure.ReviewConfigureViewModel
import com.zeafen.petwalker.presentation.walkers.walkerDetails.WalkerDetailsViewModel
import com.zeafen.petwalker.presentation.walkers.walkersList.WalkersPageViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
expect val platformAppModule: Module

val repositoryModule = module {
    singleOf(::PetWalkerAssignmentsRepository).bind<AssignmentsRepository>()
    singleOf(::PetWalkerAuthRepository).bind<AuthRepository>()
    singleOf(::PetWalkerChannelsRepository).bind<ChannelsRepository>()
    singleOf(::PetWalkerPetsRepository).bind<PetsRepository>()
    singleOf(::PetWalkerPostsRepository).bind<PostsRepository>()
    singleOf(::PetWalkerProfileRepository).bind<ProfileRepository>()
    singleOf(::PetWalkerRecruitmentsRepository).bind<RecruitmentsRepository>()
    singleOf(::PetWalkerReviewsRepository).bind<ReviewsRepository>()
    singleOf(::PetWalkerUsersRepository).bind<UsersRepository>()
}

val viewModelModule = module {
    //auth
    viewModelOf(::AuthViewModel)
    viewModelOf(::ForgotPasswordViewModel)

    //assignments
    viewModelOf(::AssignmentConfigureViewModel)
    viewModelOf(::AssignmentDetailsViewModel)
    viewModelOf(::AssignmentsViewModel)
    viewModelOf(::RecruitmentsPageViewModel)

    //channel
    viewModelOf(::ChannelDetailsViewModel)

    //home
    viewModelOf(::HomePageViewModel)
    //map
    viewModelOf(::MapScreenViewModel)
    //profile
    viewModelOf(::ProfilePageViewModel)

    //pets
    viewModelOf(::PetConfigureViewModel)
    viewModelOf(::PetDetailsViewModel)
    viewModelOf(::PetsPageViewModel)

    //posts
    viewModelOf(::PostsPageViewModel)
    viewModelOf(::PostDetailsViewModel)
    viewModelOf(::PostConfigureViewModel)

    //walkers
    viewModelOf(::WalkerDetailsViewModel)
    viewModelOf(::WalkersPageViewModel)

    //reviews|complaints
    viewModelOf(::ReviewConfigureViewModel)
    viewModelOf(::ComplaintConfigureViewModel)
}
