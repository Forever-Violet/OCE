-- 系统用户
CREATE TABLE if not exists sys_user (
  id bigint NOT NULL COMMENT 'id',
  username varchar(50) NOT NULL COMMENT '用户名',
  password varchar(100) COMMENT '密码',
  real_name varchar(50) COMMENT '姓名',
  head_url varchar(200) COMMENT '头像',
  gender tinyint unsigned COMMENT '性别   0：男   1：女    2：保密',
  email varchar(100) COMMENT '邮箱',
  mobile varchar(100) COMMENT '手机号',
  school_id bigint COMMENT '学校ID',
  super_admin tinyint unsigned COMMENT '超级管理员   0：否   1：是',
  status tinyint COMMENT '状态  0：停用   1：正常',
  creator bigint COMMENT '创建者',
  create_date datetime COMMENT '创建时间',
  updater bigint COMMENT '更新者',
  update_date datetime COMMENT '更新时间',
  primary key (id),
  unique key uk_username (username),
  key idx_create_date (create_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户';



-- 题目表 qb：question bank 题库
CREATE TABLE if not exists qb_question (
    id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '题目ID',
    content text COMMENT '题目内容，限制20000字',
    type tinyint COMMENT '题目类型   0：单选   1：多选   2：判断   3：填空   4：主观',
    analysis text COMMENT '题目解析，限制8000字',
    difficulty tinyint COMMENT '题目难度   1 ~ 5  ',
    scope tinyint COMMENT '题目适用范围   0：学校公共题库   1：账号私有题库',
    score float COMMENT '题目分值',
    course_id bigint COMMENT '课程ID',
    creator bigint COMMENT '创建者',
    create_date datetime COMMENT '创建时间',
    update_date datetime COMMENT '更新时间',
    key idx_create_date (create_date),
    key idx_course_id (course_id)

)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题库_题目表';

-- 题目选项+答案表
CREATE TABLE if not exists qb_question_option (
    id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '题目选项ID',
    content TEXT COMMENT '对于选择题此字段即为选项内容，对于填空、判断、填空、主观题此字段为空',
    answer text COMMENT '选择题此字段为true表示选项为答案，判断题（正确/错误），填空、主观题此字段内容即为答案',
    question_id bigint NOT NULL COMMENT '题目ID',
    create_date datetime COMMENT '创建时间',
    update_date datetime COMMENT '更新时间',
    key idx_create_date (create_date),
    key idx_question_id (question_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题库_题目选项+答案表';

-- 试卷表 ep: exam paper
CREATE TABLE if not exists ep_exam_paper (
    id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '试卷ID',
    title varchar(255) NOT NULL COMMENT '试卷标题',
    course_id bigint NOT NULL COMMENT '课程ID',
    type tinyint COMMENT '试卷类型，0 手动组卷；1 智能组卷',
    score float COMMENT '试卷总分',
    question_count int COMMENT '题目数量',
    difficulty float COMMENT '试卷平均难度',
    remark text COMMENT '试卷介绍',
    scope tinyint COMMENT '试卷公开状态，0 公开；1 私有',
    creator bigint COMMENT '创建者',
    create_date datetime COMMENT '创建时间',
    key idx_create_date (create_date),
    key idx_course_id (course_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷表';

-- 试卷小标题表
CREATE TABLE if not exists exam_subtitle (
    id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT 'ID',
    exam_subtitle_name text NOT NULL COMMENT '小标题名称',
    exam_paper_id bigint NOT NULL COMMENT '试卷ID',
    sort int COMMENT '标题排序',
    create_date datetime COMMENT '创建时间',
    key idx_create_date (create_date),
    key idx_exam_paper_id (exam_paper_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷小标题表';

-- 试卷小标题-题目关系表
CREATE TABLE if not exists exam_subtitle_question (
    id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT 'ID',
    exam_subtitle_id bigint NOT NULL COMMENT '小标题ID',
    question_id bigint NOT NULL COMMENT '题目ID',
    create_date datetime COMMENT '创建时间',
    key idx_create_date (create_date),
    key idx_exam_subtitle_id (exam_subtitle_id),
    key idx_question_id (question_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷小标题-题目关系表';

-- 组卷参数配置表
CREATE TABLE if not exists exam_generate_params (
    id bigint PRIMARY KEY NOT NULL COMMENT 'ID',
    config_name varchar(255) NOT NULL COMMENT '配置名称',
    population_size int COMMENT '种群大小',
    max_iterator_num int COMMENT '最大迭代次数',
    mutation_rate float COMMENT '变异概率',
    crossover_rate float COMMENT '交叉概率',
    expected_fitness float COMMENT '期望适应度',
    accuracy float COMMENT '准确率',
    use_times int COMMENT '使用次数',
    scope tinyint COMMENT '组卷配置公开状态，0 公开；1 私有',
    school_id bigint COMMENT '学校ID',
    create_date datetime COMMENT '创建时间',
    creator bigint COMMENT '创建者',
    key idx_create_date (create_date),
    key idx_school_id (school_id),
    key idx_config_name (config_name),
    key idx_creator (creator)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组卷参数配置表';

-- 考试发布表 /to do
CREATE TABLE if not exists published_exam (
    id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT 'ID',
    exam_paper_id bigint NOT NULL COMMENT '考卷ID',
    status tinyint COMMENT '状态 0 未开始， 1 进行， 2 批改，3 结束',
    start_time datetime COMMENT '开始时间',
    end_time datetime  COMMENT '截止时间',
    create_date datetime COMMENT '创建时间',
    key idx_create_date (create_date),
    key idx_exam_paper_id (exam_paper_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试发布表';

-- 学生答卷表 /to do
CREATE TABLE if not exists exam_student (
    id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT 'ID',
    exam_paper_id bigint NOT NULL COMMENT '考卷ID',
    student_id bigint NOT NULL COMMENT '学生ID',
    published_exam_id bigint NOT NULL COMMENT '考试ID',
    score float COMMENT '考试得分',
    start_time datetime COMMENT '作答开始时间',
    end_time datetime  COMMENT '作答结束时间',
    create_date datetime COMMENT '创建时间',
    key idx_create_date (create_date),
    key idx_published_exam_id (published_exam_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试发布表';

-- 学生考试 答案表 /to do
CREATE TABLE if not exists exam_student_answer (
    id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT 'ID',
    exam_student_id bigint NOT NULL COMMENT '学生答卷ID',
    question_id bigint COMMENT '题目ID',
    question_answer text COMMENT '学生答案，选择题此字段为题目选项ID，其他题型为答案',
    score float COMMENT '答案得分',
    create_date datetime COMMENT '创建时间',
    key idx_create_date (create_date),
    key idx_exam_student_id_question_id (question_id, exam_student_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试发布表';


-- 课程表
CREATE TABLE if not exists course (
    id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '课程ID',
    course_name VARCHAR(255) COMMENT '课程名称',
    grade_id bigint COMMENT '年级ID',
    remark varchar(255) COMMENT '课程介绍',
    hours int COMMENT '课程总课时',
    status tinyint COMMENT '状态 0正常 1结课',
    creator bigint COMMENT '创建者',
    create_date datetime COMMENT '创建时间',
    key idx_create_date (create_date),
    key idx_grade_id (grade_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程信息表';

-- 课程-教师关系表
CREATE TABLE if not exists course_teacher (
    id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT 'ID',
    course_id bigint NOT NULL COMMENT '课程ID',
    teacher_id bigint NOT NULL COMMENT '教师ID',
    key idx_course_id (course_id),
    key idx_teacher_id (teacher_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程-教师关系表';

-- 学校表
CREATE TABLE if not exists sys_school (
    school_id BIGINT PRIMARY KEY COMMENT '学校ID',
    school_name VARCHAR(255) COMMENT '学校名称',
    alias VARCHAR(255) COMMENT '别名',
    full_name VARCHAR(255) COMMENT '全称',
    status tinyint COMMENT '状态 0正常 1禁用',
    education_bureau VARCHAR(255) COMMENT '所属教育局',
    create_date datetime COMMENT '创建时间',
    unit_type tinyint COMMENT '单位类型 0省级 1市级 2区县级',
    key idx_create_date (create_date)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学校管理';


-- 学校学期表
CREATE TABLE if not exists sys_school_semester (
      id bigint PRIMARY KEY COMMENT 'id',
      school_id BIGINT NOT NULL COMMENT '学校ID',
      semester_name VARCHAR(255) NOT NULL COMMENT '学期名称',
      start_date DATE NOT NULL COMMENT '开始日期',
      end_date DATE NOT NULL COMMENT '结束日期',
      remark varchar(100) COMMENT '备注',
      create_date datetime COMMENT '创建时间',
      key idx_create_date (create_date),
      key idx_school_id (school_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学期管理';

-- 学校年级表
CREATE TABLE if not exists sys_school_grade (
    id bigint PRIMARY KEY COMMENT 'id',
    school_id BIGINT NOT NULL COMMENT '学校ID',
    grade_name VARCHAR(255) NOT NULL COMMENT '年级名称',
    remark varchar(100) COMMENT '备注',
    create_date datetime COMMENT '创建时间',
    key idx_create_date (create_date),
    key idx_school_id (school_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='年级管理';

-- 学校班级表
CREATE TABLE if not exists sys_school_class (
    id bigint PRIMARY KEY COMMENT 'id',
    school_id BIGINT NOT NULL COMMENT '学校ID',
    grade_id BIGINT NOT NULL COMMENT '年级ID',
    class_name VARCHAR(255) NOT NULL COMMENT '班级名称',
    remark varchar(100) COMMENT '备注',
    create_date datetime COMMENT '创建时间',
    key idx_grade_id (grade_id),
    key idx_school_id (school_id),
    key idx_create_date (create_date)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级管理';


-- 班级-用户表
CREATE TABLE if not exists sys_user_class (
    id bigint PRIMARY KEY COMMENT 'id',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    class_id BIGINT NOT NULL COMMENT '班级ID',
    create_date datetime COMMENT '创建时间',
    key idx_class_id (class_id),
    key idx_user_id (user_id),
    key idx_create_date (create_date)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户班级关系';


-- 角色管理
create table if not exists sys_role
(
  id                   bigint NOT NULL COMMENT 'id',
  name                 varchar(50) COMMENT '角色名称',
  remark               varchar(100) COMMENT '备注',
  school_id              bigint COMMENT '学校ID',
  creator              bigint COMMENT '创建者',
  create_date          datetime COMMENT '创建时间',
  updater              bigint COMMENT '更新者',
  update_date          datetime COMMENT '更新时间',
  primary key (id),
  key idx_school_id (school_id)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='角色管理';

-- 菜单管理
create table if not exists sys_menu
(
  id                   bigint NOT NULL COMMENT 'id',
  pid                  bigint COMMENT '上级ID，一级菜单为0',
  name                 varchar(200) COMMENT '名称',
  url                  varchar(200) COMMENT '菜单URL',
  permissions          varchar(500) COMMENT '授权(多个用逗号分隔，如：sys:user:list,sys:user:save)',
  menu_type            tinyint unsigned COMMENT '类型   0：菜单   1：按钮',
  icon                 varchar(50) COMMENT '菜单图标',
  sort                 int COMMENT '排序',
  creator              bigint COMMENT '创建者',
  create_date          datetime COMMENT '创建时间',
  updater              bigint COMMENT '更新者',
  update_date          datetime COMMENT '更新时间',
  primary key (id),
  key idx_pid (pid),
  key idx_sort (sort)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='菜单管理';

-- 角色用户关系
create table if not exists sys_role_user
(
  id                   bigint NOT NULL COMMENT 'id',
  role_id              bigint COMMENT '角色ID',
  user_id              bigint COMMENT '用户ID',
  creator              bigint COMMENT '创建者',
  create_date          datetime COMMENT '创建时间',
  primary key (id),
  key idx_role_id (role_id),
  key idx_user_id (user_id)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='角色用户关系';

-- 角色菜单关系
create table if not exists sys_role_menu
(
  id                   bigint NOT NULL COMMENT 'id',
  role_id              bigint COMMENT '角色ID',
  menu_id              bigint COMMENT '菜单ID',
  creator              bigint COMMENT '创建者',
  create_date          datetime COMMENT '创建时间',
  primary key (id),
  key idx_role_id (role_id),
  key idx_menu_id (menu_id)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='角色菜单关系';

-- 角色数据权限
create table if not exists sys_role_data_scope
(
  id                   bigint NOT NULL COMMENT 'id',
  role_id              bigint COMMENT '角色ID',
  school_id              bigint COMMENT '学校ID',
  creator              bigint COMMENT '创建者',
  create_date          datetime COMMENT '创建时间',
  primary key (id),
  key idx_role_id (role_id)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='角色数据权限';

-- 参数管理
create table if not exists sys_params
(
  id                   bigint NOT NULL COMMENT 'id',
  param_code           varchar(32) COMMENT '参数编码',
  param_value          varchar(2000) COMMENT '参数值',
  param_type           tinyint unsigned default 1 COMMENT '类型   0：系统参数   1：非系统参数',
  remark               varchar(200) COMMENT '备注',
  creator              bigint COMMENT '创建者',
  create_date          datetime COMMENT '创建时间',
  updater              bigint COMMENT '更新者',
  update_date          datetime COMMENT '更新时间',
  primary key (id),
  unique key uk_param_code (param_code),
  key idx_create_date (create_date)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='参数管理';

-- 字典类型
create table if not exists sys_dict_type
(
    id                   bigint NOT NULL COMMENT 'id',
    dict_type            varchar(100) NOT NULL COMMENT '字典类型',
    dict_name            varchar(255) NOT NULL COMMENT '字典名称',
    remark               varchar(255) COMMENT '备注',
    sort                 int unsigned COMMENT '排序',
    creator              bigint COMMENT '创建者',
    create_date          datetime COMMENT '创建时间',
    updater              bigint COMMENT '更新者',
    update_date          datetime COMMENT '更新时间',
    primary key (id),
    UNIQUE KEY(dict_type)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='字典类型';

-- 字典数据
create table if not exists sys_dict_data
(
    id                   bigint NOT NULL COMMENT 'id',
    dict_type_id         bigint NOT NULL COMMENT '字典类型ID',
    dict_label           varchar(255) NOT NULL COMMENT '字典标签',
    dict_value           varchar(255) COMMENT '字典值',
    remark               varchar(255) COMMENT '备注',
    sort                 int unsigned COMMENT '排序',
    creator              bigint COMMENT '创建者',
    create_date          datetime COMMENT '创建时间',
    updater              bigint COMMENT '更新者',
    update_date          datetime COMMENT '更新时间',
    primary key (id),
    unique key uk_dict_type_value (dict_type_id, dict_value),
    key idx_sort (sort)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='字典数据';

-- 登录日志
create table if not exists sys_log_login
(
  id                   bigint NOT NULL COMMENT 'id',
  operation            tinyint unsigned COMMENT '用户操作   0：用户登录   1：用户退出',
  status               tinyint unsigned NOT NULL COMMENT '状态  0：失败    1：成功    2：账号已锁定',
  user_agent           varchar(500) COMMENT '用户代理',
  ip                   varchar(32) COMMENT '操作IP',
  creator_name         varchar(50) COMMENT '用户名',
  creator              bigint COMMENT '创建者',
  create_date          datetime COMMENT '创建时间',
  primary key (id),
  key idx_status (status),
  key idx_create_date (create_date)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='登录日志';

-- 操作日志
create table if not exists sys_log_operation
(
  id                   bigint NOT NULL COMMENT 'id',
  operation            varchar(50) COMMENT '用户操作',
  request_uri          varchar(200) COMMENT '请求URI',
  request_method       varchar(20) COMMENT '请求方式',
  request_params       text COMMENT '请求参数',
  request_time         int unsigned NOT NULL COMMENT '请求时长(毫秒)',
  user_agent           varchar(500) COMMENT '用户代理',
  ip                   varchar(32) COMMENT '操作IP',
  status               tinyint unsigned NOT NULL COMMENT '状态  0：失败   1：成功',
  creator_name         varchar(50) COMMENT '用户名',
  creator              bigint COMMENT '创建者',
  create_date          datetime COMMENT '创建时间',
  primary key (id),
  key idx_create_date (create_date)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='操作日志';

-- 异常日志
create table if not exists sys_log_error
(
  id                   bigint NOT NULL COMMENT 'id',
  request_uri          varchar(200) COMMENT '请求URI',
  request_method       varchar(20) COMMENT '请求方式',
  request_params       text COMMENT '请求参数',
  user_agent           varchar(500) COMMENT '用户代理',
  ip                   varchar(32) COMMENT '操作IP',
  error_info           text COMMENT '异常信息',
  creator              bigint COMMENT '创建者',
  create_date          datetime COMMENT '创建时间',
  primary key (id),
  key idx_create_date (create_date)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='异常日志';


-- 文件上传
CREATE TABLE if not exists sys_oss (
  id bigint NOT NULL COMMENT 'id',
  url varchar(200) COMMENT 'URL地址',
  creator bigint COMMENT '创建者',
  create_date datetime COMMENT '创建时间',
  PRIMARY KEY (id),
  key idx_create_date (create_date)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='文件上传';

-- 定时任务
CREATE TABLE if not exists schedule_job (
  id bigint NOT NULL COMMENT 'id',
  bean_name varchar(200) DEFAULT NULL COMMENT 'spring bean名称',
  params varchar(2000) DEFAULT NULL COMMENT '参数',
  cron_expression varchar(100) DEFAULT NULL COMMENT 'cron表达式',
  status tinyint unsigned COMMENT '任务状态  0：暂停  1：正常',
  remark varchar(255) DEFAULT NULL COMMENT '备注',
  creator bigint COMMENT '创建者',
  create_date datetime COMMENT '创建时间',
  updater bigint COMMENT '更新者',
  update_date datetime COMMENT '更新时间',
  PRIMARY KEY (id),
  key idx_create_date (create_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务';

-- 定时任务日志
CREATE TABLE if not exists schedule_job_log (
  id bigint NOT NULL COMMENT 'id',
  job_id bigint NOT NULL COMMENT '任务id',
  bean_name varchar(200) DEFAULT NULL COMMENT 'spring bean名称',
  params varchar(2000) DEFAULT NULL COMMENT '参数',
  status tinyint unsigned NOT NULL COMMENT '任务状态    0：失败    1：成功',
  error varchar(2000) DEFAULT NULL COMMENT '失败信息',
  times int NOT NULL COMMENT '耗时(单位：毫秒)',
  create_date datetime COMMENT '创建时间',
  PRIMARY KEY (id),
  key idx_job_id (job_id),
  key idx_create_date (create_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务日志';

-- 系统用户Token
CREATE TABLE if not exists sys_user_token (
  id bigint NOT NULL COMMENT 'id',
  user_id bigint NOT NULL COMMENT '用户id',
  token varchar(100) NOT NULL COMMENT '用户token',
  expire_date datetime COMMENT '过期时间',
  update_date datetime COMMENT '更新时间',
  create_date datetime COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY user_id (user_id),
  UNIQUE KEY token (token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户Token';

-- 初始数据
INSERT INTO sys_user(id, username, password, real_name, gender, email, mobile, status, school_id, super_admin, creator, create_date, updater, update_date) VALUES (1067246875800000001, 'admin', '$2a$10$012Kx2ba5jzqr9gLlG4MX.bnQJTD9UWqF57XDo2N3.fPtLne02u/m', '管理员', 0, 'root@renren.io', '13612345678', 1, null, 1, 1067246875800000001, now(), 1067246875800000001, now());

INSERT IGNORE INTO `sys_menu` (`id`, `pid`, `name`, `url`, `permissions`, `menu_type`, `icon`, `sort`, `creator`, `create_date`, `updater`, `update_date`) VALUES
	(1067246875800000002, 0, '用户管理', NULL, '', 0, 'icon-safetycertificate', 2, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2024-03-15 13:43:26'),
	(1067246875800000003, 1067246875800000055, '新增', NULL, 'sys:user:save,sys:role:list', 1, NULL, 1, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-11-11 15:32:04'),
	(1067246875800000004, 1067246875800000055, '修改', NULL, 'sys:user:update,sys:role:list', 1, NULL, 2, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-11-11 15:31:49'),
	(1067246875800000005, 1067246875800000055, '删除', NULL, 'sys:user:delete', 1, NULL, 3, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000006, 1067246875800000055, '导出', NULL, 'sys:user:export', 1, NULL, 4, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000007, 1067246875800000002, '角色管理', 'sys/role', NULL, 0, 'icon-team', 1, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-22 19:22:57'),
	(1067246875800000008, 1067246875800000007, '查看', NULL, 'sys:role:page,sys:role:info', 1, NULL, 0, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000009, 1067246875800000007, '新增', NULL, 'sys:role:save,sys:menu:select,sys:school:list', 1, NULL, 1, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-11-11 17:05:17'),
	(1067246875800000010, 1067246875800000007, '修改', NULL, 'sys:role:update,sys:menu:select,sys:school:list', 1, NULL, 2, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-11-11 17:05:23'),
	(1067246875800000011, 1067246875800000007, '删除', NULL, 'sys:role:delete', 1, NULL, 3, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000025, 1067246875800000035, '菜单管理', 'sys/menu', NULL, 0, 'icon-unorderedlist', 0, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000026, 1067246875800000025, '查看', NULL, 'sys:menu:list,sys:menu:info', 1, NULL, 0, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000027, 1067246875800000025, '新增', NULL, 'sys:menu:save', 1, NULL, 1, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000028, 1067246875800000025, '修改', NULL, 'sys:menu:update', 1, NULL, 2, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000029, 1067246875800000025, '删除', NULL, 'sys:menu:delete', 1, NULL, 3, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000030, 1067246875800000035, '定时任务', 'job/schedule', NULL, 0, 'icon-dashboard', 3, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000031, 1067246875800000030, '查看', NULL, 'sys:schedule:page,sys:schedule:info', 1, NULL, 0, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000032, 1067246875800000030, '新增', NULL, 'sys:schedule:save', 1, NULL, 1, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000033, 1067246875800000030, '修改', NULL, 'sys:schedule:update', 1, NULL, 2, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000034, 1067246875800000030, '删除', NULL, 'sys:schedule:delete', 1, NULL, 3, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000035, 0, '系统设置', NULL, NULL, 0, 'icon-setting', 4, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2024-04-10 15:51:53'),
	(1067246875800000036, 1067246875800000030, '暂停', NULL, 'sys:schedule:pause', 1, NULL, 4, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000037, 1067246875800000030, '恢复', NULL, 'sys:schedule:resume', 1, NULL, 5, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000038, 1067246875800000030, '立即执行', NULL, 'sys:schedule:run', 1, NULL, 6, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000039, 1067246875800000030, '日志列表', NULL, 'sys:schedule:log', 1, NULL, 7, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000040, 1067246875800000035, '参数管理', 'sys/params', '', 0, 'icon-fileprotect', 1, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000041, 1067246875800000035, '字典管理', 'sys/dict-type', NULL, 0, 'icon-golden-fill', 2, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000042, 1067246875800000041, '查看', NULL, 'sys:dict:page,sys:dict:info', 1, NULL, 0, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000043, 1067246875800000041, '新增', NULL, 'sys:dict:save', 1, NULL, 1, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000044, 1067246875800000041, '修改', NULL, 'sys:dict:update', 1, NULL, 2, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000045, 1067246875800000041, '删除', NULL, 'sys:dict:delete', 1, NULL, 3, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000046, 0, '日志管理', NULL, NULL, 0, 'icon-container', 7, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2024-03-15 13:44:05'),
	(1067246875800000047, 1067246875800000035, '文件上传', 'oss/oss', 'sys:oss:all', 0, 'icon-upload', 4, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000048, 1067246875800000046, '登录日志', 'sys/log-login', 'sys:log:login', 0, 'icon-filedone', 0, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000049, 1067246875800000046, '操作日志', 'sys/log-operation', 'sys:log:operation', 0, 'icon-solution', 1, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000050, 1067246875800000046, '异常日志', 'sys/log-error', 'sys:log:error', 0, 'icon-file-exception', 2, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000051, 1067246875800000053, 'SQL监控', '{{ApiUrl}}/druid/sql.html', NULL, 0, 'icon-database', 0, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000053, 0, '系统监控', NULL, NULL, 0, 'icon-desktop', 9, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2024-04-10 15:52:00'),
	(1067246875800000055, 1067246875800000002, '用户列表', 'sys/user', NULL, 0, 'icon-user', 0, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-22 17:51:11'),
	(1067246875800000056, 1067246875800000055, '查看', NULL, 'sys:user:page,sys:user:info', 1, NULL, 0, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000057, 1067246875800000040, '新增', NULL, 'sys:params:save', 1, NULL, 1, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000058, 1067246875800000040, '导出', NULL, 'sys:params:export', 1, NULL, 4, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000059, 1067246875800000040, '查看', '', 'sys:params:page,sys:params:info', 1, NULL, 0, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000060, 1067246875800000040, '修改', NULL, 'sys:params:update', 1, NULL, 2, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1067246875800000061, 1067246875800000040, '删除', '', 'sys:params:delete', 1, '', 3, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1156748733921165314, 1067246875800000053, '接口文档', '{{ApiUrl}}/doc.html', '', 0, 'icon-file-word', 1, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1718257406296743938, 1737415108751011842, '学校列表', 'sys/school', '', 0, 'icon-home', 0, 1067246875800000001, '2023-10-28 21:24:24', 1067246875800000001, '2023-12-20 18:12:06'),
	(1718288185865383937, 1718257406296743938, '新增', '', 'sys:school:save', 1, '', 3, 1067246875800000001, '2023-10-28 23:26:42', 1067246875800000001, '2023-10-29 14:44:30'),
	(1718517396983074818, 1718257406296743938, '删除', '', 'sys:school:delete', 1, '', 4, 1067246875800000001, '2023-10-29 14:37:31', 1067246875800000001, '2023-10-29 14:44:35'),
	(1718519134716776450, 1718257406296743938, '查看', NULL, 'sys:school:page,sys:school:info,sys:school:list', 1, NULL, 0, 1067246875800000001, '2023-10-29 14:44:25', 1067246875800000001, '2023-11-11 17:05:45'),
	(1718529633617530881, 1718257406296743938, '修改', '', 'sys:school:update', 1, '', 2, 1067246875800000001, '2023-10-29 15:26:08', 1067246875800000001, '2023-10-30 19:11:54'),
	(1729788012698861570, 1737415108751011842, '班级管理', 'sys/class', '', 0, 'icon-solution', 4, 1067246875800000001, '2023-11-29 17:02:55', 1067246875800000001, '2023-12-20 18:12:38'),
	(1729790202588745729, 1729788012698861570, '查看', '', 'sys:class:page,sys:class:info,sys:class:list', 1, '', 0, 1067246875800000001, '2023-11-29 17:11:37', 1067246875800000001, '2023-11-29 17:11:37'),
	(1729790287301103617, 1729788012698861570, '修改', '', 'sys:class:update', 1, '', 1, 1067246875800000001, '2023-11-29 17:11:57', 1067246875800000001, '2023-11-29 17:11:57'),
	(1729790349511020545, 1729788012698861570, '新增', '', 'sys:class:save', 1, '', 3, 1067246875800000001, '2023-11-29 17:12:12', 1067246875800000001, '2023-11-29 17:12:12'),
	(1729790387700158466, 1729788012698861570, '删除', '', 'sys:class:delete', 1, '', 2, 1067246875800000001, '2023-11-29 17:12:21', 1067246875800000001, '2023-12-01 21:03:05'),
	(1729861203135942657, 1737415108751011842, '年级管理', 'sys/grade', '', 0, 'icon-group', 3, 1067246875800000001, '2023-11-29 21:53:45', 1067246875800000001, '2023-12-20 18:12:30'),
	(1729861489346859009, 1729861203135942657, '查看', '', 'sys:grade:page,sys:grade:info,sys:grade:list', 1, '', 0, 1067246875800000001, '2023-11-29 21:54:53', 1067246875800000001, '2023-11-29 21:54:53'),
	(1729861593319460865, 1729861203135942657, '修改', '', 'sys:grade:update', 1, '', 1, 1067246875800000001, '2023-11-29 21:55:18', 1067246875800000001, '2023-11-29 21:55:18'),
	(1729861668514942978, 1729861203135942657, '新增', '', 'sys:grade:save', 1, '', 2, 1067246875800000001, '2023-11-29 21:55:36', 1067246875800000001, '2023-11-29 21:55:36'),
	(1729861766653267970, 1729861203135942657, '删除', '', 'sys:grade:delete', 1, '', 3, 1067246875800000001, '2023-11-29 21:55:59', 1067246875800000001, '2023-11-29 21:55:59'),
	(1729861824085872641, 1729861203135942657, '导出', '', 'sys:grade:export', 1, '', 4, 1067246875800000001, '2023-11-29 21:56:13', 1067246875800000001, '2023-11-29 21:56:13'),
	(1729861981519073281, 1718257406296743938, '导出', '', 'sys:school:export', 1, '', 5, 1067246875800000001, '2023-11-29 21:56:50', 1067246875800000001, '2023-11-29 21:56:58'),
	(1729862096635940866, 1729788012698861570, '导出', '', 'sys:class:export', 1, '', 4, 1067246875800000001, '2023-11-29 21:57:18', 1067246875800000001, '2023-11-29 21:57:18'),
	(1730573161583362049, 1737415108751011842, '学期管理', 'sys/semester', '', 1, 'icon-Field-time', 1, 1067246875800000001, '2023-12-01 21:02:49', 1067246875800000001, '2023-12-20 18:12:22'),
	(1730573361634885634, 1730573161583362049, '查看', '', 'sys:semester:page,sys:semester:list,sys:semester:info', 1, '', 0, 1067246875800000001, '2023-12-01 21:03:37', 1067246875800000001, '2023-12-01 21:03:37'),
	(1730573432468291585, 1730573161583362049, '新增', '', 'sys:semester:save', 1, '', 1, 1067246875800000001, '2023-12-01 21:03:53', 1067246875800000001, '2023-12-01 21:03:53'),
	(1730573475539599361, 1730573161583362049, '修改', '', 'sys:semester:update', 1, '', 3, 1067246875800000001, '2023-12-01 21:04:04', 1067246875800000001, '2023-12-01 21:04:04'),
	(1730573530870857729, 1730573161583362049, '删除', '', 'sys:semester:delete', 1, '', 2, 1067246875800000001, '2023-12-01 21:04:17', 1067246875800000001, '2023-12-01 21:04:17'),
	(1730573607127498754, 1730573161583362049, '导出', '', 'sys:semester:export', 1, '', 4, 1067246875800000001, '2023-12-01 21:04:35', 1067246875800000001, '2023-12-01 21:04:35'),
	(1737415108751011842, 0, '学校管理', '', '', 0, 'icon-bank-fill', 3, 1067246875800000001, '2023-12-20 18:10:16', 1067246875800000001, '2024-03-15 13:43:37'),
	(1768510085080535041, 0, '考卷管理', '', '', 0, 'icon-file-text', 1, 1067246875800000001, '2024-03-15 13:30:36', 1067246875800000001, '2024-03-15 13:42:00'),
	(1768512837177298946, 0, '题库管理', '', '', 0, 'icon-container', 0, 1067246875800000001, '2024-03-15 13:41:32', 1067246875800000001, '2024-03-15 13:41:32'),
	(1769557064013164545, 1769560363606065154, '课程列表', 'course/course', NULL, 0, 'icon-desktop', 0, 1067246875800000001, '2024-03-18 11:01:40', 1067246875800000001, '2024-03-18 11:04:46'),
	(1769557064013164546, 1769557064013164545, '查看', NULL, 'course:course:page,course:course:info', 1, NULL, 0, 1067246875800000001, '2024-03-18 11:01:40', 1067246875800000001, '2024-03-18 11:01:40'),
	(1769557064013164547, 1769557064013164545, '新增', NULL, 'course:course:save', 1, NULL, 1, 1067246875800000001, '2024-03-18 11:01:41', 1067246875800000001, '2024-03-18 11:01:41'),
	(1769557064013164548, 1769557064013164545, '修改', NULL, 'course:course:update', 1, NULL, 2, 1067246875800000001, '2024-03-18 11:01:41', 1067246875800000001, '2024-03-18 11:01:41'),
	(1769557064013164549, 1769557064013164545, '删除', NULL, 'course:course:delete', 1, NULL, 3, 1067246875800000001, '2024-03-18 11:01:41', 1067246875800000001, '2024-03-18 11:01:41'),
	(1769557064013164550, 1769557064013164545, '导出', NULL, 'course:course:export', 1, NULL, 4, 1067246875800000001, '2024-03-18 11:01:41', 1067246875800000001, '2024-03-18 11:01:41'),
	(1769560363606065154, 0, '课程管理', '', '', 0, 'icon-read', 0, 1067246875800000001, '2024-03-18 11:04:02', 1067246875800000001, '2024-03-18 11:04:02'),
	(1769563228962914306, 1768510085080535041, '考卷列表', 'exam/exampaper', NULL, 0, 'icon-desktop', 0, 1067246875800000001, '2024-03-18 11:17:35', 1067246875800000001, '2024-03-22 18:17:30'),
	(1769563228962914307, 1769563228962914306, '查看', NULL, 'exam:epexampaper:page,exam:epexampaper:info', 1, NULL, 0, 1067246875800000001, '2024-03-18 11:17:35', 1067246875800000001, '2024-03-18 11:17:35'),
	(1769563228962914308, 1769563228962914306, '新增', NULL, 'exam:epexampaper:save', 1, NULL, 1, 1067246875800000001, '2024-03-18 11:17:35', 1067246875800000001, '2024-03-18 11:17:35'),
	(1769563228962914309, 1769563228962914306, '修改', NULL, 'exam:epexampaper:update', 1, NULL, 2, 1067246875800000001, '2024-03-18 11:17:35', 1067246875800000001, '2024-03-18 11:17:35'),
	(1769563228962914310, 1769563228962914306, '删除', NULL, 'exam:epexampaper:delete', 1, NULL, 3, 1067246875800000001, '2024-03-18 11:17:35', 1067246875800000001, '2024-03-18 11:17:35'),
	(1769563228962914311, 1769563228962914306, '导出', NULL, 'exam:epexampaper:export', 1, NULL, 4, 1067246875800000001, '2024-03-18 11:17:35', 1067246875800000001, '2024-04-07 21:33:01'),
	(1769563320180637698, 1768512837177298946, '题目列表', 'question/question', NULL, 0, 'icon-bulb-fill', 0, 1067246875800000001, '2024-03-18 11:17:34', 1067246875800000001, '2024-03-18 11:31:10'),
	(1769563320180637699, 1769563320180637698, '查看', NULL, 'question:question:page,question:question:info', 1, NULL, 0, 1067246875800000001, '2024-03-18 11:17:34', 1067246875800000001, '2024-03-18 11:30:12'),
	(1769563320180637700, 1769563320180637698, '新增', NULL, 'question:question:save', 1, NULL, 1, 1067246875800000001, '2024-03-18 11:17:34', 1067246875800000001, '2024-03-18 11:30:20'),
	(1769563320180637701, 1769563320180637698, '修改', NULL, 'question:question:update', 1, NULL, 2, 1067246875800000001, '2024-03-18 11:17:34', 1067246875800000001, '2024-03-18 11:30:26'),
	(1769563320180637702, 1769563320180637698, '删除', NULL, 'question:question:delete', 1, NULL, 3, 1067246875800000001, '2024-03-18 11:17:34', 1067246875800000001, '2024-03-18 11:30:40'),
	(1769563320180637703, 1769563320180637698, '导出', NULL, 'question:question:export', 1, NULL, 4, 1067246875800000001, '2024-03-18 11:17:34', 1067246875800000001, '2024-03-18 11:30:33'),
	(1770984346193780737, 1769557064013164545, '教师列表', '', 'sys:user:teacher', 1, '', 6, 1067246875800000001, '2024-03-22 09:22:26', 1067246875800000001, '2024-04-09 19:44:53'),
	(1770984354766938114, 1769557064013164545, '列表', '', 'course:course:list', 1, '', 6, 1067246875800000001, '2024-03-22 09:22:28', 1067246875800000001, '2024-03-22 09:22:28'),
	(1771790586469773314, 1769563320180637698, '列表', '', 'question:question:list', 1, '', 10, 1067246875800000001, '2024-03-24 14:46:09', 1067246875800000001, '2024-03-24 14:46:09'),
	(1772931208477921282, 1769563228962914306, '一键生成试卷', '', 'exam:epexampaper:generate', 1, '', 7, 1067246875800000001, '2024-03-27 18:18:34', 1067246875800000001, '2024-03-27 18:18:34'),
	(1777664581243379714, 1769557064013164545, '年级列表', '', 'sys:grade:list', 1, '', 8, 1067246875800000001, '2024-04-09 19:47:18', 1067246875800000001, '2024-04-09 19:47:18'),
	(1777967054990577665, 1067246875800000007, '导出', '', 'sys:role:export', 1, '', 7, 1067246875800000001, '2024-04-10 15:49:14', 1067246875800000001, '2024-04-10 15:49:14'),
	(1789496999531618306, 1768510085080535041, '组卷配置', 'exam/generate-params', NULL, 0, 'icon-desktop', 0, 1067246875800000001, '2024-05-12 11:26:04', 1067246875800000001, '2024-05-12 11:36:39'),
	(1789496999531618307, 1789496999531618306, '查看', NULL, 'exam:generateparams:page,exam:generateparams:info,exam:generateparams:list', 1, NULL, 0, 1067246875800000001, '2024-05-12 11:26:04', 1067246875800000001, '2024-05-12 16:47:25'),
	(1789496999531618308, 1789496999531618306, '新增', NULL, 'exam:generateparams:save', 1, NULL, 1, 1067246875800000001, '2024-05-12 11:26:04', 1067246875800000001, '2024-05-12 11:34:49'),
	(1789496999531618309, 1789496999531618306, '修改', NULL, 'exam:generateparams:update', 1, NULL, 2, 1067246875800000001, '2024-05-12 11:26:04', 1067246875800000001, '2024-05-12 11:34:54'),
	(1789496999531618310, 1789496999531618306, '删除', NULL, 'exam:generateparams:delete', 1, NULL, 3, 1067246875800000001, '2024-05-12 11:26:04', 1067246875800000001, '2024-05-12 11:35:11'),
	(1789496999531618311, 1789496999531618306, '导出', NULL, 'exam:generateparams:export', 1, NULL, 4, 1067246875800000001, '2024-05-12 11:26:04', 1067246875800000001, '2024-05-12 11:35:16');

INSERT IGNORE INTO `sys_dict_type` (`id`, `dict_type`, `dict_name`, `remark`, `sort`, `creator`, `create_date`, `updater`, `update_date`) VALUES
	(1160061077912858625, 'gender', '性别', '', 0, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1225813644059140097, 'notice_type', '站内通知-类型', '', 1, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1718520998321541121, 'schoolStatus', '学校状态', '', 3, 1067246875800000001, '2023-10-29 14:51:49', 1067246875800000001, '2023-10-31 11:01:38'),
	(1719181886602891266, 'unitType', '单位类型', '', 4, 1067246875800000001, '2023-10-31 10:37:57', 1067246875800000001, '2023-10-31 10:37:57'),
	(1769637551883632642, 'questionType', '题目类型', '5个', 6, 1067246875800000001, '2024-03-18 16:10:45', 1067246875800000001, '2024-03-18 16:10:58'),
	(1769638225857953794, 'examPaperType', '试卷类型', '', 5, 1067246875800000001, '2024-03-18 16:13:26', 1067246875800000001, '2024-03-18 16:13:26'),
	(1769638584881987585, 'courseStatus', '课程状态', '', 8, 1067246875800000001, '2024-03-18 16:14:52', 1067246875800000001, '2024-03-18 16:14:52'),
	(1769638959332671490, 'questionScope', '题目适用范围', '', 10, 1067246875800000001, '2024-03-18 16:16:21', 1067246875800000001, '2024-03-18 16:16:21'),
	(1773260111888920577, 'questionTypeEnumName', '题型Enum名称', '', 7, 1067246875800000001, '2024-03-28 16:05:31', 1067246875800000001, '2024-03-28 16:05:31'),
	(1774272687705001985, 'examPaperScope', '试卷适用范围', '0 公共，1 私有', 12, 1067246875800000001, '2024-03-31 11:09:08', 1067246875800000001, '2024-03-31 11:09:08'),
	(1789501340549636097, 'examGenerateParamsScope', '组卷参数适用范围', '0公共，1私有', 13, 1067246875800000001, '2024-05-12 11:42:22', 1067246875800000001, '2024-05-12 11:42:22');


INSERT IGNORE INTO `sys_dict_data` (`id`, `dict_type_id`, `dict_label`, `dict_value`, `remark`, `sort`, `creator`, `create_date`, `updater`, `update_date`) VALUES
	(1160061112075464705, 1160061077912858625, '男', '0', '', 0, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1160061146967879681, 1160061077912858625, '女', '1', '', 1, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1160061190127267841, 1160061077912858625, '保密', '2', '', 2, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1225814069634195457, 1225813644059140097, '公告', '0', '', 0, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1225814107559092225, 1225813644059140097, '会议', '1', '', 1, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1225814271879340034, 1225813644059140097, '其他', '2', '', 2, 1067246875800000001, '2023-10-20 17:26:05', 1067246875800000001, '2023-10-20 17:26:05'),
	(1718521119461429249, 1718520998321541121, '正常', '0', '', 0, 1067246875800000001, '2023-10-29 14:52:18', 1067246875800000001, '2023-10-29 14:52:18'),
	(1718521152621596674, 1718520998321541121, '停用', '1', '', 1, 1067246875800000001, '2023-10-29 14:52:26', 1067246875800000001, '2023-10-29 14:52:26'),
	(1719181999836516354, 1719181886602891266, '省级', '0', '', 0, 1067246875800000001, '2023-10-31 10:38:24', 1067246875800000001, '2023-10-31 10:38:24'),
	(1719182048276533250, 1719181886602891266, '市级', '1', '', 1, 1067246875800000001, '2023-10-31 10:38:36', 1067246875800000001, '2023-10-31 10:38:36'),
	(1719182114135494657, 1719181886602891266, '区县级', '2', '', 2, 1067246875800000001, '2023-10-31 10:38:52', 1067246875800000001, '2023-10-31 10:38:52'),
	(1722556771268354050, 1721808833577234433, '国家级', '0', '', 0, 1067246875800000001, '2023-11-09 18:08:32', 1067246875800000001, '2023-11-09 18:08:32'),
	(1722556811382677506, 1721808833577234433, '省级', '1', '', 1, 1067246875800000001, '2023-11-09 18:08:42', 1067246875800000001, '2023-11-09 18:08:42'),
	(1722556840889606146, 1721808833577234433, '市厅级', '2', '', 2, 1067246875800000001, '2023-11-09 18:08:49', 1067246875800000001, '2023-11-09 18:08:49'),
	(1722556865933795330, 1721808833577234433, '区级', '3', '', 3, 1067246875800000001, '2023-11-09 18:08:55', 1067246875800000001, '2023-11-09 18:08:55'),
	(1722556890520805378, 1721808833577234433, '校级', '4', '', 4, 1067246875800000001, '2023-11-09 18:09:01', 1067246875800000001, '2023-11-09 18:09:01'),
	(1722829686377086978, 1721808582929821697, '劳动实践', '5', '', 5, 1067246875800000001, '2023-11-10 12:13:01', 1067246875800000001, '2023-11-10 12:13:01'),
	(1769637715100778497, 1769637551883632642, '单选题', '0', '', 0, 1067246875800000001, '2024-03-18 16:11:24', 1067246875800000001, '2024-03-18 16:11:24'),
	(1769637757857513474, 1769637551883632642, '多选题', '1', '', 2, 1067246875800000001, '2024-03-18 16:11:34', 1067246875800000001, '2024-03-18 16:11:34'),
	(1769637816380637185, 1769637551883632642, '判断题', '2', '', 4, 1067246875800000001, '2024-03-18 16:11:48', 1067246875800000001, '2024-03-18 16:11:48'),
	(1769637909578072066, 1769637551883632642, '填空题', '3', '', 8, 1067246875800000001, '2024-03-18 16:12:11', 1067246875800000001, '2024-03-18 16:12:16'),
	(1769638003551453185, 1769637551883632642, '主观题', '4', '', 9, 1067246875800000001, '2024-03-18 16:12:33', 1067246875800000001, '2024-03-18 16:12:33'),
	(1769638291255541761, 1769638225857953794, '手动组卷', '0', '', 0, 1067246875800000001, '2024-03-18 16:13:42', 1067246875800000001, '2024-03-18 16:13:42'),
	(1769638354673418242, 1769638225857953794, '智能组卷', '1', '', 1, 1067246875800000001, '2024-03-18 16:13:57', 1067246875800000001, '2024-03-18 16:13:57'),
	(1769638659683205122, 1769638584881987585, '正常', '0', '', 0, 1067246875800000001, '2024-03-18 16:15:09', 1067246875800000001, '2024-03-18 16:15:09'),
	(1769638693485101058, 1769638584881987585, '结课', '1', '', 1, 1067246875800000001, '2024-03-18 16:15:17', 1067246875800000001, '2024-03-18 16:15:17'),
	(1769639055327707137, 1769638959332671490, '公共', '0', '学校公共', 0, 1067246875800000001, '2024-03-18 16:16:44', 1067246875800000001, '2024-03-18 16:16:44'),
	(1769639107844587521, 1769638959332671490, '私有', '1', '账号私有', 1, 1067246875800000001, '2024-03-18 16:16:56', 1067246875800000001, '2024-03-18 16:16:56'),
	(1773260320589099009, 1773260111888920577, 'SINGLE_CHOICE', '0', '单选题', 0, 1067246875800000001, '2024-03-28 16:06:21', 1067246875800000001, '2024-03-28 16:06:21'),
	(1773260383419772930, 1773260111888920577, 'MULTIPLE_CHOICE', '1', '多选题', 1, 1067246875800000001, '2024-03-28 16:06:36', 1067246875800000001, '2024-03-28 16:06:36'),
	(1773260449542975489, 1773260111888920577, 'JUDGMENTAL', '2', '判断题', 2, 1067246875800000001, '2024-03-28 16:06:51', 1067246875800000001, '2024-03-28 16:06:51'),
	(1773260498528251905, 1773260111888920577, 'COMPLETION', '3', '填空题', 3, 1067246875800000001, '2024-03-28 16:07:03', 1067246875800000001, '2024-03-28 16:07:03'),
	(1773260539955392514, 1773260111888920577, 'SUBJECTIVE', '4', '主观题', 4, 1067246875800000001, '2024-03-28 16:07:13', 1067246875800000001, '2024-03-28 16:07:13'),
	(1774272757485637633, 1774272687705001985, '公共', '0', '', 0, 1067246875800000001, '2024-03-31 11:09:24', 1067246875800000001, '2024-03-31 11:09:24'),
	(1774272789332987905, 1774272687705001985, '私有', '1', '', 1, 1067246875800000001, '2024-03-31 11:09:32', 1067246875800000001, '2024-03-31 11:09:32'),
	(1789501411919912961, 1789501340549636097, '公共', '0', '', 0, 1067246875800000001, '2024-05-12 11:42:39', 1067246875800000001, '2024-05-12 11:42:39'),
	(1789501433503801346, 1789501340549636097, '私有', '1', '', 1, 1067246875800000001, '2024-05-12 11:42:44', 1067246875800000001, '2024-05-12 11:42:44');

INSERT IGNORE INTO `sys_role` (`id`, `name`, `remark`, `school_id`, `creator`, `create_date`, `updater`, `update_date`) VALUES
	(1767092707517755394, '老师', '佛山大学老师', 1767032694734508033, 1067246875800000001, '2024-03-11 15:38:27', 1067246875800000001, '2024-05-12 16:15:16'),
	(1767102282236788737, '老师', '佛山一中老师', 1767102200762433537, 1067246875800000001, '2024-03-11 16:16:30', 1067246875800000001, '2024-05-12 16:15:21'),
	(1779063088031129602, '普通管理员', '佛山大学管理员', 1767032694734508033, 1067246875800000001, '2024-04-13 16:24:28', 1067246875800000001, '2024-05-12 16:15:25');

INSERT INTO `sys_role_data_scope` (`id`, `role_id`, `school_id`, `creator`, `create_date`) VALUES
	(1777664713263292417, 1767092707517755394, 1767032694734508033, 1067246875800000001, '2024-04-09 19:47:50'),
	(1777664777796853761, 1767102282236788737, 1767102200762433537, 1067246875800000001, '2024-04-09 19:48:05'),
	(1779396138507231234, 1779063088031129602, 1767032694734508033, 1067246875800000001, '2024-04-14 14:27:54');

INSERT IGNORE INTO `sys_role_menu` (`id`, `role_id`, `menu_id`, `creator`, `create_date`) VALUES
	(1789570020178173953, 1767092707517755394, 1768512837177298946, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020178173954, 1767092707517755394, 1769563320180637698, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020178173955, 1767092707517755394, 1769563320180637699, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020178173956, 1767092707517755394, 1769563320180637700, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020178173957, 1767092707517755394, 1769563320180637701, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020178173958, 1767092707517755394, 1769563320180637702, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020178173959, 1767092707517755394, 1769563320180637703, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020178173960, 1767092707517755394, 1771790586469773314, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020178173961, 1767092707517755394, 1769560363606065154, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020178173962, 1767092707517755394, 1769557064013164545, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020232699906, 1767092707517755394, 1769557064013164546, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020232699907, 1767092707517755394, 1769557064013164547, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020232699908, 1767092707517755394, 1769557064013164548, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020232699909, 1767092707517755394, 1769557064013164549, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020232699910, 1767092707517755394, 1769557064013164550, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020232699911, 1767092707517755394, 1770984346193780737, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020232699912, 1767092707517755394, 1770984354766938114, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020232699913, 1767092707517755394, 1777664581243379714, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020232699914, 1767092707517755394, 1768510085080535041, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020232699915, 1767092707517755394, 1769563228962914306, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020232699916, 1767092707517755394, 1769563228962914307, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020232699917, 1767092707517755394, 1769563228962914308, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020232699918, 1767092707517755394, 1769563228962914309, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020232699919, 1767092707517755394, 1769563228962914310, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020232699920, 1767092707517755394, 1769563228962914311, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020232699921, 1767092707517755394, 1772931208477921282, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020232699922, 1767092707517755394, 1789496999531618306, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020295614466, 1767092707517755394, 1789496999531618307, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020295614467, 1767092707517755394, 1789496999531618308, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020295614468, 1767092707517755394, 1789496999531618309, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020295614469, 1767092707517755394, 1789496999531618310, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570020295614470, 1767092707517755394, 1789496999531618311, 1067246875800000001, '2024-05-12 16:15:16'),
	(1789570039060930562, 1767102282236788737, 1768512837177298946, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039128039426, 1767102282236788737, 1769563320180637698, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039128039427, 1767102282236788737, 1769563320180637699, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039128039428, 1767102282236788737, 1769563320180637700, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039128039429, 1767102282236788737, 1769563320180637701, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039128039430, 1767102282236788737, 1769563320180637702, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039128039431, 1767102282236788737, 1769563320180637703, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039128039432, 1767102282236788737, 1771790586469773314, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039128039433, 1767102282236788737, 1769560363606065154, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039128039434, 1767102282236788737, 1769557064013164545, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039128039435, 1767102282236788737, 1769557064013164546, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039128039436, 1767102282236788737, 1769557064013164547, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039128039437, 1767102282236788737, 1769557064013164548, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039190953985, 1767102282236788737, 1769557064013164549, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039190953986, 1767102282236788737, 1769557064013164550, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039190953987, 1767102282236788737, 1770984346193780737, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039190953988, 1767102282236788737, 1770984354766938114, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039190953989, 1767102282236788737, 1777664581243379714, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039190953990, 1767102282236788737, 1768510085080535041, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039190953991, 1767102282236788737, 1769563228962914306, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039190953992, 1767102282236788737, 1769563228962914307, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039190953993, 1767102282236788737, 1769563228962914308, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039190953994, 1767102282236788737, 1769563228962914309, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039190953995, 1767102282236788737, 1769563228962914310, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039190953996, 1767102282236788737, 1769563228962914311, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039190953997, 1767102282236788737, 1772931208477921282, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039190953998, 1767102282236788737, 1789496999531618306, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039258062849, 1767102282236788737, 1789496999531618307, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039258062850, 1767102282236788737, 1789496999531618308, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039258062851, 1767102282236788737, 1789496999531618309, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039258062852, 1767102282236788737, 1789496999531618310, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570039258062853, 1767102282236788737, 1789496999531618311, 1067246875800000001, '2024-05-12 16:15:21'),
	(1789570059168423938, 1779063088031129602, 1067246875800000002, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059168423939, 1779063088031129602, 1737415108751011842, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059168423940, 1779063088031129602, 1768512837177298946, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059168423941, 1779063088031129602, 1769563320180637698, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059239727105, 1779063088031129602, 1769563320180637699, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059239727106, 1779063088031129602, 1769563320180637700, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059239727107, 1779063088031129602, 1769563320180637701, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059239727108, 1779063088031129602, 1769563320180637702, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059239727109, 1779063088031129602, 1769563320180637703, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059239727110, 1779063088031129602, 1771790586469773314, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059239727111, 1779063088031129602, 1769560363606065154, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059239727112, 1779063088031129602, 1769557064013164545, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059239727113, 1779063088031129602, 1769557064013164546, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059239727114, 1779063088031129602, 1769557064013164547, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059239727115, 1779063088031129602, 1769557064013164548, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059239727116, 1779063088031129602, 1769557064013164549, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059239727117, 1779063088031129602, 1769557064013164550, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059239727118, 1779063088031129602, 1770984346193780737, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059239727119, 1779063088031129602, 1770984354766938114, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059239727120, 1779063088031129602, 1777664581243379714, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059239727121, 1779063088031129602, 1768510085080535041, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059306835970, 1779063088031129602, 1769563228962914306, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059306835971, 1779063088031129602, 1769563228962914307, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059306835972, 1779063088031129602, 1769563228962914308, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059306835973, 1779063088031129602, 1769563228962914309, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059306835974, 1779063088031129602, 1769563228962914310, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059306835975, 1779063088031129602, 1769563228962914311, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059306835976, 1779063088031129602, 1772931208477921282, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059306835977, 1779063088031129602, 1789496999531618306, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059306835978, 1779063088031129602, 1789496999531618307, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059306835979, 1779063088031129602, 1789496999531618308, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059306835980, 1779063088031129602, 1789496999531618309, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059306835981, 1779063088031129602, 1789496999531618310, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059306835982, 1779063088031129602, 1789496999531618311, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059306835983, 1779063088031129602, 1067246875800000055, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059306835984, 1779063088031129602, 1067246875800000056, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059306835985, 1779063088031129602, 1067246875800000003, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059369750529, 1779063088031129602, 1067246875800000004, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059369750530, 1779063088031129602, 1067246875800000005, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059369750531, 1779063088031129602, 1067246875800000006, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059369750532, 1779063088031129602, 1729861203135942657, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059369750533, 1779063088031129602, 1729861489346859009, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059369750534, 1779063088031129602, 1729861593319460865, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059369750535, 1779063088031129602, 1729861668514942978, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059369750536, 1779063088031129602, 1729861766653267970, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059369750537, 1779063088031129602, 1729861824085872641, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059369750538, 1779063088031129602, 1729788012698861570, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059369750539, 1779063088031129602, 1729790202588745729, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059369750540, 1779063088031129602, 1729790287301103617, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059369750541, 1779063088031129602, 1729790387700158466, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059369750542, 1779063088031129602, 1729790349511020545, 1067246875800000001, '2024-05-12 16:15:25'),
	(1789570059369750543, 1779063088031129602, 1729862096635940866, 1067246875800000001, '2024-05-12 16:15:25');

INSERT IGNORE INTO `sys_role_user` (`id`, `role_id`, `user_id`, `creator`, `create_date`) VALUES
	(1777638343908921346, 1767092707517755394, 1767091618668048385, 1067246875800000001, '2024-04-09 18:03:03'),
	(1778723497092665346, 1767092707517755394, 1778723239872778241, 1067246875800000001, '2024-04-12 17:55:03'),
	(1782613987136458754, 1767102282236788737, 1782613985513263105, 1067246875800000001, '2024-04-23 11:34:29'),
	(1785212431768334337, 1779063088031129602, 1779062895277694978, 1067246875800000001, '2024-04-30 15:39:46'),
	(1788051489483866114, 1767092707517755394, 1788051343060713474, 1067246875800000001, '2024-05-08 11:41:10');

INSERT IGNORE INTO `sys_user` (`id`, `username`, `password`, `real_name`, `head_url`, `gender`, `email`, `mobile`, `school_id`, `super_admin`, `status`, `creator`, `create_date`, `updater`, `update_date`) VALUES
	(1067246875800000001, 'admin', '$2a$10$012Kx2ba5jzqr9gLlG4MX.bnQJTD9UWqF57XDo2N3.fPtLne02u/m', '管理员', NULL, 0, 'root@renren.io', '13612345678', NULL, 1, 1, 1067246875800000001, '2024-04-26 14:46:35', 1067246875800000001, '2024-04-26 14:46:35'),
	(1767091618668048385, '1', '$2a$10$f9/1jsogmtUN3VmPIneoM.A7LPDJqI7oq1kGILlCsw.CY4G5kDeZu', '东方坛', NULL, 0, '', '', 1767032694734508033, 0, 1, 1067246875800000001, '2024-03-11 15:34:08', 1067246875800000001, '2024-04-09 18:03:02'),
	(1778723239872778241, '2', '$2a$10$/rSe/7TWg5Sc79HtVYZwE.18n0QE5XobHCM5fJeq7rRTJdu0IwAsC', '张怀义', NULL, 0, '120392823@qq.com', '13172200093', 1767032694734508033, 0, 1, 1067246875800000001, '2024-04-12 17:54:02', 1067246875800000001, '2024-04-12 17:55:03'),
	(1779062895277694978, '333', '$2a$10$55XLBy1b0jHc0H..2PdKoeRAp3BQ0ucZaelEupQtPCWp8wABzXm9W', '佛大管理员', NULL, 0, '', '13182300203', 1767032694734508033, 0, 1, 1067246875800000001, '2024-04-13 16:23:42', 1067246875800000001, '2024-04-30 15:39:46'),
	(1782613985513263105, 'yizhong', '$2a$10$E4P231p4FLIIshsKlEmRwOApZU2kZDoWXFVC.PY48ipoxMuWvH1Wu', 'admin', NULL, 0, '', '', 1767102200762433537, 0, 1, 1067246875800000001, '2024-04-23 11:34:28', 1067246875800000001, '2024-04-23 11:34:28'),
	(1788051343060713474, 'hzm', '$2a$10$l1gcO5P08y3ZWCeFaJ3kiOsQwaA.nOVw9KWtmoAjC4Zhd/btYs8sO', '黄', NULL, 0, '1328264935@qq.com', '', 1767032694734508033, 0, 1, 1067246875800000001, '2024-05-08 11:40:35', 1067246875800000001, '2024-05-08 11:41:10');

INSERT IGNORE INTO `sys_user_class` (`id`, `user_id`, `class_id`, `create_date`) VALUES
	(1778723498250293249, 1778723239872778241, 1778723393157812226, '2024-04-12 17:55:04'),
	(1778723498833301506, 1778723239872778241, 1778723442684153858, '2024-04-12 17:55:04');

INSERT INTO sys_params(id, param_code, param_value, param_type, remark, creator, create_date, updater, update_date) VALUES (1067246875800000073, 'CLOUD_STORAGE_CONFIG_KEY', '{"type":1,"qiniuDomain":"http://test.oss.renren.io","qiniuPrefix":"upload","qiniuAccessKey":"NrgMfABZxWLo5B-YYSjoE8-AZ1EISdi1Z3ubLOeZ","qiniuSecretKey":"uIwJHevMRWU0VLxFvgy0tAcOdGqasdtVlJkdy6vV","qiniuBucketName":"renren-oss","aliyunDomain":"","aliyunPrefix":"","aliyunEndPoint":"","aliyunAccessKeyId":"","aliyunAccessKeySecret":"","aliyunBucketName":"","qcloudDomain":"","qcloudPrefix":"","qcloudSecretId":"","qcloudSecretKey":"","qcloudBucketName":""}', '0', '云存储配置信息', 1067246875800000001, now(), 1067246875800000001, now());

INSERT INTO `schedule_job` (`id`, `bean_name`, `params`, `cron_expression`, `status`, `remark`, `creator`, `create_date`, `updater`, `update_date`) VALUES
	(1737776343862489090, 'databaseBackupTask', '{host: 127.0.0.1, port: 3306, database: ocepgen}', '0 0 2 * * ?', 1, '数据库定时备份，每天半夜两点执行', 1067246875800000001, '2023-12-21 18:05:41', 1067246875800000001, '2023-12-21 18:07:22'),
	(1737793301534334978, 'databaseBackupCleanUpTask', '{daysToKeep: 14}', '0 0 3 * * ?', 1, '保留14天以内的数据库备份文件，删除过期备份文件，每天半夜3点执行', 1067246875800000001, '2023-12-21 19:13:04', 1067246875800000001, '2023-12-21 19:19:19');

INSERT IGNORE INTO `sys_school` (`school_id`, `school_name`, `alias`, `full_name`, `status`, `education_bureau`, `create_date`, `unit_type`) VALUES
	(1767032694734508033, '佛山大学', '佛大', '佛山大学', 0, '广东省教育局', '2024-03-11 11:39:59', 0),
	(1767102200762433537, '佛山一中', '一中', '佛山一中', 0, '佛山市教育局', '2024-03-11 16:16:10', 1);

INSERT IGNORE INTO `sys_school_class` (`id`, `school_id`, `grade_id`, `class_name`, `remark`, `create_date`) VALUES
	(1778723393157812226, 1767032694734508033, 1770716571663699970, '20计科5班', '1', '2024-04-12 17:54:39'),
	(1778723442684153858, 1767032694734508033, 1770716571663699970, '20计科8班', '2', '2024-04-12 17:54:50');

INSERT IGNORE INTO `sys_school_grade` (`id`, `school_id`, `grade_name`, `remark`, `create_date`) VALUES
	(1770716571663699970, 1767032694734508033, '20级计算机科学与技术', '20级计算机科学与技术', '2024-03-21 15:38:24');


--  quartz自带表结构
CREATE TABLE if not exists QRTZ_JOB_DETAILS(
  SCHED_NAME VARCHAR(120) NOT NULL,
  JOB_NAME VARCHAR(200) NOT NULL,
  JOB_GROUP VARCHAR(200) NOT NULL,
  DESCRIPTION VARCHAR(250) NULL,
  JOB_CLASS_NAME VARCHAR(250) NOT NULL,
  IS_DURABLE VARCHAR(1) NOT NULL,
  IS_NONCONCURRENT VARCHAR(1) NOT NULL,
  IS_UPDATE_DATA VARCHAR(1) NOT NULL,
  REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
  JOB_DATA BLOB NULL,
  PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE if not exists QRTZ_TRIGGERS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  JOB_NAME VARCHAR(200) NOT NULL,
  JOB_GROUP VARCHAR(200) NOT NULL,
  DESCRIPTION VARCHAR(250) NULL,
  NEXT_FIRE_TIME BIGINT(13) NULL,
  PREV_FIRE_TIME BIGINT(13) NULL,
  PRIORITY INTEGER NULL,
  TRIGGER_STATE VARCHAR(16) NOT NULL,
  TRIGGER_TYPE VARCHAR(8) NOT NULL,
  START_TIME BIGINT(13) NOT NULL,
  END_TIME BIGINT(13) NULL,
  CALENDAR_NAME VARCHAR(200) NULL,
  MISFIRE_INSTR SMALLINT(2) NULL,
  JOB_DATA BLOB NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
  REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE if not exists QRTZ_SIMPLE_TRIGGERS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  REPEAT_COUNT BIGINT(7) NOT NULL,
  REPEAT_INTERVAL BIGINT(12) NOT NULL,
  TIMES_TRIGGERED BIGINT(10) NOT NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE if not exists QRTZ_CRON_TRIGGERS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  CRON_EXPRESSION VARCHAR(120) NOT NULL,
  TIME_ZONE_ID VARCHAR(80),
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE if not exists QRTZ_SIMPROP_TRIGGERS
(
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  STR_PROP_1 VARCHAR(512) NULL,
  STR_PROP_2 VARCHAR(512) NULL,
  STR_PROP_3 VARCHAR(512) NULL,
  INT_PROP_1 INT NULL,
  INT_PROP_2 INT NULL,
  LONG_PROP_1 BIGINT NULL,
  LONG_PROP_2 BIGINT NULL,
  DEC_PROP_1 NUMERIC(13,4) NULL,
  DEC_PROP_2 NUMERIC(13,4) NULL,
  BOOL_PROP_1 VARCHAR(1) NULL,
  BOOL_PROP_2 VARCHAR(1) NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE if not exists QRTZ_BLOB_TRIGGERS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  BLOB_DATA BLOB NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  INDEX (SCHED_NAME,TRIGGER_NAME, TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE if not exists QRTZ_CALENDARS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  CALENDAR_NAME VARCHAR(200) NOT NULL,
  CALENDAR BLOB NOT NULL,
  PRIMARY KEY (SCHED_NAME,CALENDAR_NAME))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE if not exists QRTZ_PAUSED_TRIGGER_GRPS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE if not exists QRTZ_FIRED_TRIGGERS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  ENTRY_ID VARCHAR(95) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  INSTANCE_NAME VARCHAR(200) NOT NULL,
  FIRED_TIME BIGINT(13) NOT NULL,
  SCHED_TIME BIGINT(13) NOT NULL,
  PRIORITY INTEGER NOT NULL,
  STATE VARCHAR(16) NOT NULL,
  JOB_NAME VARCHAR(200) NULL,
  JOB_GROUP VARCHAR(200) NULL,
  IS_NONCONCURRENT VARCHAR(1) NULL,
  REQUESTS_RECOVERY VARCHAR(1) NULL,
  PRIMARY KEY (SCHED_NAME,ENTRY_ID))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE if not exists QRTZ_SCHEDULER_STATE (
  SCHED_NAME VARCHAR(120) NOT NULL,
  INSTANCE_NAME VARCHAR(200) NOT NULL,
  LAST_CHECKIN_TIME BIGINT(13) NOT NULL,
  CHECKIN_INTERVAL BIGINT(13) NOT NULL,
  PRIMARY KEY (SCHED_NAME,INSTANCE_NAME))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE if not exists QRTZ_LOCKS (
  SCHED_NAME VARCHAR(120) NOT NULL,
  LOCK_NAME VARCHAR(40) NOT NULL,
  PRIMARY KEY (SCHED_NAME,LOCK_NAME))
  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX IDX_QRTZ_J_REQ_RECOVERY ON QRTZ_JOB_DETAILS(SCHED_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_J_GRP ON QRTZ_JOB_DETAILS(SCHED_NAME,JOB_GROUP);

CREATE INDEX IDX_QRTZ_T_J ON QRTZ_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_JG ON QRTZ_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_T_C ON QRTZ_TRIGGERS(SCHED_NAME,CALENDAR_NAME);
CREATE INDEX IDX_QRTZ_T_G ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_T_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_N_G_STATE ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NEXT_FIRE_TIME ON QRTZ_TRIGGERS(SCHED_NAME,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST ON QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
CREATE INDEX IDX_QRTZ_T_NFT_ST_MISFIRE_GRP ON QRTZ_TRIGGERS(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);

CREATE INDEX IDX_QRTZ_FT_TRIG_INST_NAME ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME);
CREATE INDEX IDX_QRTZ_FT_INST_JOB_REQ_RCVRY ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
CREATE INDEX IDX_QRTZ_FT_J_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_JG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,JOB_GROUP);
CREATE INDEX IDX_QRTZ_FT_T_G ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
CREATE INDEX IDX_QRTZ_FT_TG ON QRTZ_FIRED_TRIGGERS(SCHED_NAME,TRIGGER_GROUP);
