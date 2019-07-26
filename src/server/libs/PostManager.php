<?php
class PostManager {
    public static function get_timeline() {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "SELECT user.user_id, user.user_name, user.icon, post_id, content,
               (SELECT file_name FROM post_image WHERE order_number = 1 AND post_id = post_data.id) as image01,
               (SELECT file_name FROM post_image WHERE order_number = 2 AND post_id = post_data.id) as image02
                FROM post_data
                LEFT JOIN user ON post_data.user_id = user.id
                WHERE delete_flg = false
                ORDER BY timestamp DESC";
        $data = $db->fetchAll($sql, array());
        return $data;
    }

    public static function get_user_timeline($user_id) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "SELECT user.user_id, user.user_name, user.icon, post_id, content,
               (SELECT file_name FROM post_image WHERE order_number = 1 AND post_id = post_data.id) as image01,
               (SELECT file_name FROM post_image WHERE order_number = 2 AND post_id = post_data.id) as image02
                FROM post_data
                LEFT JOIN user ON post_data.user_id = user.id
                WHERE post_data.user_id = :user_id
                AND delete_flg = false
                ORDER BY timestamp DESC";
        $data = $db->fetchAll($sql, array(
            'user_id' => $user_id
        ));
        return $data;
    }

    public static function get_image($post_id) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "SELECT file_name FROM post_image WHERE post_id = :post_id";
        $data = $db->fetchAll($sql, array('post_id' => $post_id));
        return $data;
    }

    public static function get_post($post_id, $user_id) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "SELECT pd.post_id, pd.content, DATE_FORMAT(FROM_UNIXTIME(pd.timestamp), '%Y年%c月%d日 %k時%i分') as timestamp,
                       pd.anime_id, ma.anime_name, pd.chara_id, mc.chara_name,
                       user.user_id, user.user_name, user.icon,
                       CASE WHEN (SELECT id FROM favorite_post WHERE user_id = :user_id AND post_id = pd.id) IS NULL
                           THEN false ELSE true END AS favorite_flg,
                       (SELECT file_name FROM post_image WHERE order_number = 1 AND post_id = pd.id) as image01,
                       (SELECT file_name FROM post_image WHERE order_number = 2 AND post_id = pd.id) as image02
                FROM post_data as pd
                LEFT JOIN user ON pd.user_id = user.id
                LEFT JOIN master_anime as ma ON pd.anime_id = ma.id
                LEFT JOIN master_chara as mc ON pd.chara_id = mc.id
                WHERE pd.post_id = :post_id AND delete_flg = false";
        $data = $db->fetch($sql, array('post_id' => $post_id, 'user_id' => $user_id));
        if (is_array($data) && count($data) != 0) {
            return $data;
        } else {
            return null;
        }
    }

    public static function get_post_id($post_id) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "SELECT id FROM post_data WHERE post_id = :post_id";
        $data = $db->fetchColumn($sql, array('post_id' => $post_id));
        if (!$data) {
            return false;
        } else {
            return $data;
        }
    }

    public static function get_thread_anime($anime_id) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        /*
        $sql = "SELECT cd.user_id, cd.post_id, cd.content, DATE_FORMAT(FROM_UNIXTIME(cd.timestamp), '%Y年%c月%d日 %k時%i分') as timestamp, u.user_id, u.user_name, u.icon, 'c' as way
                FROM comment_anime AS ca
                LEFT JOIN comment_data AS cd ON cd.id = ca.comment_id
                LEFT JOIN user AS u ON u.id = cd.user_id
                WHERE ca.anime_id = :anime_id AND cd.delete_flg = false
                UNION ALL
                SELECT pd.user_id, pd.post_id, pd.content, DATE_FORMAT(FROM_UNIXTIME(pd.timestamp), '%Y年%c月%d日 %k時%i分') as timestamp, u.user_id, u.user_name, u.icon, 'p' as way
                FROM post_data as pd
                LEFT JOIN user AS u ON u.id = pd.user_id
                WHERE anime_id = :anime_id AND delete_flg = false
                ORDER BY timestamp DESC";
        */
        $sql = "SELECT cd.user_id, cd.post_id, cd.content, DATE_FORMAT(FROM_UNIXTIME(cd.timestamp), '%Y年%c月%d日 %k時%i分') as timestamp,
                       u.user_id, u.user_name, u.icon, 'c' as way, NULL as image01, NULL as image02
                FROM comment_anime AS ca
                LEFT JOIN comment_data AS cd ON cd.id = ca.comment_id
                LEFT JOIN user AS u ON u.id = cd.user_id
                WHERE ca.anime_id = :anime_id AND cd.delete_flg = false
                UNION ALL
                SELECT pd.user_id, pd.post_id, pd.content, DATE_FORMAT(FROM_UNIXTIME(pd.timestamp), '%Y年%c月%d日 %k時%i分') as timestamp,
                       u.user_id, u.user_name, u.icon, 'p' as way,
                       (SELECT file_name FROM post_image WHERE order_number = 1 AND post_id = pd.id) as image01,
                       (SELECT file_name FROM post_image WHERE order_number = 2 AND post_id = pd.id) as image02
                FROM post_data as pd
                LEFT JOIN user AS u ON u.id = pd.user_id
                WHERE anime_id = :anime_id AND delete_flg = false
                ORDER BY timestamp DESC";
        $data = $db->fetchAll($sql, array('anime_id' => $anime_id));
        return $data;
    }

    public static function get_thread_chara($chara_id) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        /*
        $sql = "SELECT cd.user_id, cd.post_id, cd.content, DATE_FORMAT(FROM_UNIXTIME(cd.timestamp), '%Y年%c月%d日 %k時%i分') as timestamp, u.user_id, u.user_name, u.icon, 'c' as way
                FROM comment_chara AS cc
                LEFT JOIN comment_data AS cd ON cd.id = cc.comment_id
                LEFT JOIN user AS u ON u.id = cd.user_id
                WHERE cc.chara_id = :chara_id AND cd.delete_flg = false
                UNION ALL
                SELECT pd.user_id, pd.post_id, pd.content, DATE_FORMAT(FROM_UNIXTIME(pd.timestamp), '%Y年%c月%d日 %k時%i分') as timestamp, u.user_id, u.user_name, u.icon, 'p' as way
                FROM post_data as pd
                LEFT JOIN user AS u ON u.id = pd.user_id
                WHERE chara_id = :chara_id AND delete_flg = false  
                ORDER BY timestamp DESC";
        */
        $sql = "SELECT cd.user_id, cd.post_id, cd.content, DATE_FORMAT(FROM_UNIXTIME(cd.timestamp), '%Y年%c月%d日 %k時%i分') as timestamp,
                       u.user_id, u.user_name, u.icon, 'c' as way, NULl as image01, NULL as image02
                FROM comment_chara AS cc
                LEFT JOIN comment_data AS cd ON cd.id = cc.comment_id
                LEFT JOIN user AS u ON u.id = cd.user_id
                WHERE cc.chara_id = :chara_id AND cd.delete_flg = false
                UNION ALL
                SELECT pd.user_id, pd.post_id, pd.content, DATE_FORMAT(FROM_UNIXTIME(pd.timestamp), '%Y年%c月%d日 %k時%i分') as timestamp,
                       u.user_id, u.user_name, u.icon, 'p' as way,
                       (SELECT file_name FROM post_image WHERE order_number = 1 AND post_id = pd.id) as image01,
                       (SELECT file_name FROM post_image WHERE order_number = 2 AND post_id = pd.id) as image02
                FROM post_data as pd
                LEFT JOIN user AS u ON u.id = pd.user_id
                WHERE chara_id = :chara_id AND delete_flg = false
                ORDER BY timestamp DESC";
        $data = $db->fetchAll($sql, array('chara_id' => $chara_id));
        return $data;
    }

    public static function add_post($user_id, $content, $chara_id, $image_flg) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "SELECT anime_id FROM master_chara WHERE id = :chara_id";
        $anime_id = $db->fetchColumn($sql, array('chara_id' => $chara_id));
        $now_time = time();
        $post_id = md5($user_id.','.$now_time);
        $sql = "INSERT INTO post_data (user_id, post_id, content, image_flg, timestamp, anime_id, chara_id)
                VALUES (:user_id, :post_id, :content, :image_flg, :timestamp, :anime_id, :chara_id)";
        $insert_id = $db->insert($sql, array(
            'user_id' => $user_id,
            'post_id' => $post_id,
            'content' => $content,
            'image_flg' => $image_flg,
            'timestamp' => $now_time,
            'anime_id' => $anime_id,
            'chara_id' => $chara_id
        ));
        return $insert_id;
    }

    public static function add_image($post_id, $order_number, $file_name) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "INSERT INTO post_image (post_id, order_number, file_name) VALUES (:post_id, :order_number, :file_name)";
        $db->insert($sql, array(
            'post_id' => $post_id,
            'order_number' => $order_number,
            'file_name' => $file_name
        ));
    }

    public static function add_thread($user_id, $content, $anime_id, $chara_id) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();

        $sql = "INSERT INTO comment_data (user_id, post_id, content, timestamp) VALUES (:user_id, :post_id, :content, :timestamp)";
        $now_time = time();
        $post_id = md5($user_id.','.$now_time);
        $comment_id = $db->insert($sql, array(
            'user_id' => $user_id,
            'post_id' => $post_id,
            'content' => $content,
            'timestamp' => $now_time
        ));

        if ($chara_id == 0) {
            $sql = "INSERT INTO comment_anime (comment_id, anime_id) VALUES (:comment_id, :anime_id)";
            $db->insert($sql, array('comment_id' => $comment_id, 'anime_id' => $anime_id));
        } else {
            $sql = "INSERT INTO comment_chara (comment_id, chara_id) VALUES (:comment_id, :chara_id)";
            $db->insert($sql, array('comment_id' => $comment_id, 'chara_id' => $chara_id));
        }
    }

    public static function get_notice($user_id) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "SELECT * FROM notice WHERE user_id = :user_id";
        $data = $db->fetchAll($sql, array('user_id' => $user_id));
        return $data;
    }

    /*
    public static function get_anime_list() {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "SELECT * FROM master_anime ORDER BY id";
        $data = $db->fetchAll($sql, array());
        return $data;
    }
    */

    public static function get_chara_list_all() {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "SELECT master_chara.id, anime_name, chara_name
                FROM master_chara
                LEFT JOIN master_anime ON master_anime.id = master_chara.anime_id
                ORDER BY id";
        $data = $db->fetchAll($sql, array());
        return $data;
    }

    /*
    public static function get_chara_list_select($anime_id) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "SELECT id, chara_name
                FROM master_chara
                WHERE anime_id = :anime_id
                ORDER BY id";
        $data = $db->fetchAll($sql, array('anime_id' => $anime_id));
        return $data;
    }
    */

    public static function reg_favorite($user_id, $post_id) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "SELECT count(*) FROM favorite_post WHERE user_id = :user_id AND post_id = :post_id";
        $count = $db->fetchColumn($sql, array('user_id' => $user_id, 'post_id' => $post_id));
        $time = time();
        $sql = "SELECT user_id FROM post_data WHERE id = :post_id";
        $notice_user_id = $db->fetchColumn($sql, array(
            'post_id' => $post_id
        ));
        if ($count == 0) {
            $sql = "INSERT INTO favorite_post (user_id, post_id, timestamp) VALUES (:user_id, :post_id, :timestamp)";
            $favorite_id = $db->insert($sql, array(
                'user_id' => $user_id,
                'post_id' => $post_id,
                'timestamp' => $time
            ));
            $sql = "INSERT INTO notice_post (user_id, favorite_post_id) VALUES (:user_id, :favorite_post_id)";
            $db->insert($sql, array(
                'user_id' => $notice_user_id,
                'favorite_post_id' => $favorite_id
            ));
            return 'REGISTER';
        } else {
            $sql = "SELECT id FROM favorite_post WHERE user_id = :user_id AND post_id = :post_id";
            $favorite_post_id = $db->fetchColumn($sql, array(
                'user_id' => $user_id,
                'post_id' => $post_id
            ));
            $sql = "DELETE FROM favorite_post WHERE id = :id";
            $db->execute($sql, array(
                'id' => $favorite_post_id
            ));
            $sql = "DELETE FROM notice_post WHERE user_id = :user_id AND favorite_post_id = :favorite_post_id";
            $db->execute($sql, array(
                'user_id' => $notice_user_id,
                'favorite_post_id' => $favorite_post_id
            ));
            return 'UNREGISTER';
        }
    }
}