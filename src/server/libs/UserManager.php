<?php
class UserManager {
    public static function get_my_info($user_id) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "SELECT user_name, user_id, icon, profile, email FROM user WHERE id = :user_id";
        $data = $db->fetch($sql, array('user_id' => $user_id));
        return $data;
    }

    public static function get_user_number_id($user_id) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "SELECT id FROM user WHERE user_id = :user_id";
        $data = $db->fetchColumn($sql, array('user_id' => $user_id));
        if (!$data) {
            return false;
        } else {
            return $data;
        }
    }

    public static function get_favorite($user_id) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "SELECT user_name FROM user WHERE id = :user_id";
        $user_name = $db->fetchColumn($sql, array('user_id' => $user_id));
        $sql = "SELECT pd.post_id, pd.content, user.user_id, user.user_name, user.icon,
                       (SELECT file_name FROM post_image WHERE order_number = 1 AND post_id = pd.id) as image01,
                       (SELECT file_name FROM post_image WHERE order_number = 2 AND post_id = pd.id) as image02
                FROM favorite_post AS fp
                LEFT JOIN post_data as pd ON pd.id = fp.post_id
                LEFT JOIN user ON pd.user_id = user.id
                WHERE fp.user_id = :user_id
                ORDER BY pd.timestamp DESC";
        $favorite = $db->fetchAll($sql, array('user_id' => $user_id));
        $array = ['user_name' => $user_name, 'favorite' => $favorite];
        return $array;
    }

    public static function get_notice($user_id) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        /*
         * SELECT u.user_id, u.user_name, u.icon, pd.content
FROM notice_post AS np
LEFT JOIN favorite_post fp ON np.favorite_post_id = fp.post_id
LEFT JOIN post_data pd ON fp.post_id = pd.id
LEFT JOIN user u ON fp.user_id = u.id
WHERE np.user_id = 12
ORDER BY fp.timestamp DESC
         */
        /*
        $sql = "SELECT u.user_id, u.user_name, u.icon, pd.content
                FROM notice_post AS np
                LEFT JOIN post_data pd ON np.favorite_post_id = pd.id
                LEFT JOIN user u ON pd.user_id = u.id
                WHERE np.user_id = :user_id
                ORDER BY pd.timestamp DESC";
        */
        $sql = "SELECT u.user_id, u.user_name, u.icon, pd.content
                FROM notice_post AS np
                LEFT JOIN favorite_post fp ON np.favorite_post_id = fp.id
                LEFT JOIN post_data pd ON fp.post_id = pd.id
                LEFT JOIN user u ON fp.user_id = u.id
                WHERE np.user_id = :user_id
                ORDER BY fp.timestamp DESC";
        $data = $db->fetchAll($sql, array(
            'user_id' => $user_id
        ));
        return $data;
    }

    public static function get_profile($user_id) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "SELECT user_id, user_name, icon, profile FROM user WHERE id = :id";
        $data = $db->fetch($sql, array(
            'id' => $user_id
        ));
        return $data;
    }

    public static function edit_my_info($id, $user_name, $profile, $icon = null) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        if ($icon == null) {
            $sql = "UPDATE user SET user_name = :user_name, profile = :profile WHERE id = :id";
            $db->execute($sql, array(
                'user_name' => $user_name,
                'profile' => $profile,
                'id' => $id
            ));
        } else {
            $sql = "UPDATE user SET user_name = :user_name, icon = :icon, profile = :profile WHERE id = :id";
            $db->execute($sql, array(
                'user_name' => $user_name,
                'icon' => $icon,
                'profile' => $profile,
                'id' => $id
            ));
        }
    }
}