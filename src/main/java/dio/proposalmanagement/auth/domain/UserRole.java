package dio.proposalmanagement.auth.domain;

/**
 * Roles funcionais que podem ser atribuídas a um usuário.
 *
 * <p>Os nomes são persistidos e também usados como authorities pelo Spring Security.</p>
 */
public enum UserRole {
    ROLE_INFLUENCER,
    ROLE_BRAND,
}
