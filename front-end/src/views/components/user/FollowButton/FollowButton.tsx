import React, { useEffect, useState } from "react";
//
import axios from "axios";
//
import styles from "./FollowButton.module.css";
import { useNavigate } from "react-router-dom";

type Props = {
  from: string;
  to: string;
};

const FollowButton: React.FC<Props> = ({ from, to }) => {
  const navigate = useNavigate();

  // 팔로우 상태 체크
  const [isfollowing, setIsFollowing] = useState<boolean>(false);
  const checkFollow = async () => {
    try {
      const followTo: Props = {
        from: from,
        to: to,
      };

      const response = await axios.post("/api/users/follow-check", followTo);

      console.log(response.data.result);
      setIsFollowing(response.data.result);
    } catch (err) {
      console.error(err);
    }
  };

  // 팔로우 요청
  const [followed, setFollowed] = useState<string>();
  const followHandler = async (e: React.MouseEvent<HTMLButtonElement>) => {
    try {
      if (!from) {
        navigate("/signin");
        return;
      }

      const followTo: Props = {
        from: from,
        to: to,
      };

      const response = await axios.post("/api/users/follow", followTo);

      console.log(response.data.result);
      setFollowed(followed);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    checkFollow();
  }, [followed]);

  return (
    <button
      type="button"
      onClick={followHandler}
      className={styles.followButton}
    >
      {isfollowing ? "언팔로우" : "팔로우"}
    </button>
  );
};

export default FollowButton;
