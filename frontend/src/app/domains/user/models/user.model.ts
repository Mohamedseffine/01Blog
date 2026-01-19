/**
 * User Domain Model
 */
export interface User {
  id: number;
  username: string;
  email: string;
  profilePicture?: string;
  bio?: string;
  followersCount: number;
  followingCount: number;
  createdAt: Date;
}

export interface UserProfile extends User {
  isFollowing: boolean;
  posts?: any[];
}

export interface UpdateProfileDto {
  username?: string;
  bio?: string;
  profilePicture?: File;
}
