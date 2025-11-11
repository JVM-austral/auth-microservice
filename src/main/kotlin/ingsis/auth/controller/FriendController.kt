package ingsis.auth.controller

import ingsis.auth.dto.UserSubjectDto
import ingsis.auth.service.FriendService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/friends")
class FriendController(
    private val friendService: FriendService,
) {
    @GetMapping
    fun getAll(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<List<UserSubjectDto>> {
        val friends = friendService.getAll(jwt.subject)
        return ResponseEntity.ok(friends)
    }

    @GetMapping("/check/{friendId}")
    fun checkFriendship(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable friendId: String,
    ): ResponseEntity<Map<String, Boolean>> {
        val areFriends = friendService.areFriends(jwt.subject, friendId)
        return ResponseEntity.ok(mapOf("areFriends" to areFriends))
    }

    @PostMapping("/{friendId}")
    fun addFriend(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable friendId: String,
    ): ResponseEntity<Map<String, Any>> {
        val friend = friendService.addFriend(jwt.subject, friendId)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            mapOf(
                "message" to "Friend added successfully",
                "friendshipId" to friend.id,
            ),
        )
    }

    @DeleteMapping("/{friendId}")
    fun removeFriend(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable friendId: String,
    ): ResponseEntity<Map<String, String>> {
        friendService.removeFriend(jwt.subject, friendId)
        return ResponseEntity.ok(mapOf("message" to "Friend removed successfully"))
    }

    @GetMapping("/count")
    fun countFriends(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<Map<String, Long>> {
        val count = friendService.countFriends(jwt.subject)
        return ResponseEntity.ok(mapOf("count" to count))
    }
}
