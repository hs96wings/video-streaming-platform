export interface ApiSuccessResponse<T> {
  success: true;
  code: number;
  data: T;
}

export interface ApiFailResponse {
  success: false;
  code: number;
  message: string;
}

// 공통 자격 증명 타입을 정의
type UserCredentials = {
  userid: string;
  password: string;
};

export type LoginRequest = UserCredentials;

// 회원가입 요청은 공통 타입에 추가 필드를 결합(&)
export type SignupRequest = UserCredentials;
/* 현재는 없으므로 &를 쓰지 않는다
export type SingupRequest = UserCredentials & {
  nickname: string;
  email: string;
}
*/

// 응답 타입은 역할이 명확하므로 합치지 않는다
export interface LoginResponse {
  token: string;
}

export interface SignupResponse {
  token: string;
}

export interface Video {
  id: number;
  title: string;
  description: string;
  videoPath: string;
  thumbnailPath: string;
  uploadedAt: string;
  videoStatus: 'UPLOADED' | 'PROCESSING' | 'READY';
  viewCount: number;
}
