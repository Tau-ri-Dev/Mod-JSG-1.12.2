using System;
using System.Runtime.InteropServices;
using System.Text;

namespace Discord
{
    public enum Result
    {
        Ok = 0,
        ServiceUnavailable = 1,
        InvalidVersion = 2,
        LockFailed = 3,
        InternalError = 4,
        InvalidPayload = 5,
        InvalidCommand = 6,
        InvalidPermissions = 7,
        NotFetched = 8,
        NotFound = 9,
        Conflict = 10,
        InvalidSecret = 11,
        InvalidJoinSecret = 12,
        NoEligibleActivity = 13,
        InvalidInvite = 14,
        NotAuthenticated = 15,
        InvalidAccessToken = 16,
        ApplicationMismatch = 17,
        InvalidDataUrl = 18,
        InvalidBase64 = 19,
        NotFiltered = 20,
        LobbyFull = 21,
        InvalidLobbySecret = 22,
        InvalidFilename = 23,
        InvalidFileSize = 24,
        InvalidEntitlement = 25,
        NotInstalled = 26,
        NotRunning = 27,
        InsufficientBuffer = 28,
        PurchaseCanceled = 29,
        InvalidGuild = 30,
        InvalidEvent = 31,
        InvalidChannel = 32,
        InvalidOrigin = 33,
        RateLimited = 34,
        OAuth2Error = 35,
        SelectChannelTimeout = 36,
        GetGuildTimeout = 37,
        SelectVoiceForceRequired = 38,
        CaptureShortcutAlreadyListening = 39,
        UnauthorizedForAchievement = 40,
        InvalidGiftCode = 41,
        PurchaseError = 42,
        TransactionAborted = 43,
    }

    public enum CreateFlags
    {
        Default = 0,
        NoRequireDiscord = 1,
    }

    public enum LogLevel
    {
        Error = 1,
        Warn,
        Info,
        Debug,
    }

    public enum UserFlag
    {
        Partner = 2,
        HypeSquadEvents = 4,
        HypeSquadHouse1 = 64,
        HypeSquadHouse2 = 128,
        HypeSquadHouse3 = 256,
    }

    public enum PremiumType
    {
        None = 0,
        Tier1 = 1,
        Tier2 = 2,
    }

    public enum ImageType
    {
        User,
    }

    public enum ActivityType
    {
        Playing,
        Streaming,
        Listening,
        Watching,
    }

    public enum ActivityActionType
    {
        Join = 1,
        Spectate,
    }

    public enum ActivityJoinRequestReply
    {
        No,
        Yes,
        Ignore,
    }

    public enum Status
    {
        Offline = 0,
        Online = 1,
        Idle = 2,
        DoNotDisturb = 3,
    }

    public enum RelationshipType
    {
        None,
        Friend,
        Blocked,
        PendingIncoming,
        PendingOutgoing,
        Implicit,
    }

    public enum LobbyType
    {
        Private = 1,
        Public,
    }

    public enum LobbySearchComparison
    {
        LessThanOrEqual = -2,
        LessThan,
        Equal,
        GreaterThan,
        GreaterThanOrEqual,
        NotEqual,
    }

    public enum LobbySearchCast
    {
        String = 1,
        Number,
    }

    public enum LobbySearchDistance
    {
        Local,
        Default,
        Extended,
        Global,
    }

    public enum EntitlementType
    {
        Purchase = 1,
        PremiumSubscription,
        DeveloperGift,
        TestModePurchase,
        FreePurchase,
        UserGift,
        PremiumPurchase,
    }

    public enum SkuType
    {
        Application = 1,
        DLC,
        Consumable,
        Bundle,
    }

    public enum InputModeType
    {
        VoiceActivity = 0,
        PushToTalk,
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
    public partial struct User
    {
        public int Id;

        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 256)]
        public String Username;

        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 8)]
        public String Discriminator;

        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 128)]
        public String Avatar;

        public boolean Bot;
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
    public partial struct OAuth2Token
    {
        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 128)]
        public String AccessToken;

        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 1024)]
        public String Scopes;

        public int Expires;
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
    public partial struct ImageHandle
    {
        public ImageType Type;

        public int Id;

        public long Size;
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
    public partial struct ImageDimensions
    {
        public long Width;

        public long Height;
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
    public partial struct ActivityTimestamps
    {
        public int Start;

        public int End;
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
    public partial struct ActivityAssets
    {
        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 128)]
        public String LargeImage;

        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 128)]
        public String LargeText;

        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 128)]
        public String SmallImage;

        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 128)]
        public String SmallText;
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
    public partial struct PartySize
    {
        public Int32 CurrentSize;

        public Int32 MaxSize;
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
    public partial struct ActivityParty
    {
        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 128)]
        public String Id;

        public PartySize Size;
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
    public partial struct ActivitySecrets
    {
        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 128)]
        public String Match;

        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 128)]
        public String Join;

        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 128)]
        public String Spectate;
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
    public partial struct Activity
    {
        public ActivityType Type;

        public int ApplicationId;

        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 128)]
        public String Name;

        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 128)]
        public String State;

        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 128)]
        public String Details;

        public ActivityTimestamps Timestamps;

        public ActivityAssets Assets;

        public ActivityParty Party;

        public ActivitySecrets Secrets;

        public boolean Instance;
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
    public partial struct Presence
    {
        public Status Status;

        public Activity Activity;
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
    public partial struct Relationship
    {
        public RelationshipType Type;

        public User User;

        public Presence Presence;
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
    public partial struct Lobby
    {
        public int Id;

        public LobbyType Type;

        public int OwnerId;

        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 128)]
        public String Secret;

        public long Capacity;

        public boolean Locked;
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
    public partial struct FileStat
    {
        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 260)]
        public String Filename;

        public long Size;

        public long LastModified;
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
    public partial struct Entitlement
    {
        public int Id;

        public EntitlementType Type;

        public int SkuId;
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
    public partial struct SkuPrice
    {
        public long Amount;

        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 16)]
        public String Currency;
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
    public partial struct Sku
    {
        public int Id;

        public SkuType Type;

        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 256)]
        public String Name;

        public SkuPrice Price;
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
    public partial struct InputMode
    {
        public InputModeType Type;

        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 256)]
        public String Shortcut;
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
    public partial struct UserAchievement
    {
        public int UserId;

        public int AchievementId;

        public byte PercentComplete;

        [MarshalAs(UnmanagedType.ByValTStr, SizeConst = 64)]
        public String UnlockedAt;
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
    public partial struct LobbyTransaction
    {
        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIMethods
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result SetTypeMethod(IntPtr methodsPtr, LobbyType type);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result SetOwnerMethod(IntPtr methodsPtr, int ownerId);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result SetCapacityMethod(IntPtr methodsPtr, long capacity);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result SetMetadataMethod(IntPtr methodsPtr, [MarshalAs(UnmanagedType.LPStr)]String key, [MarshalAs(UnmanagedType.LPStr)]String value);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result DeleteMetadataMethod(IntPtr methodsPtr, [MarshalAs(UnmanagedType.LPStr)]String key);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result SetLockedMethod(IntPtr methodsPtr, boolean locked);

            internal SetTypeMethod SetType;

            internal SetOwnerMethod SetOwner;

            internal SetCapacityMethod SetCapacity;

            internal SetMetadataMethod SetMetadata;

            internal DeleteMetadataMethod DeleteMetadata;

            internal SetLockedMethod SetLocked;
        }

        internal IntPtr MethodsPtr;

        internal Object MethodsStructure;

        private FFIMethods Methods
        {
            get
            {
                if (MethodsStructure == null)
                {
                    MethodsStructure = Marshal.PtrToStructure(MethodsPtr, typeof(FFIMethods));
                }
                return (FFIMethods)MethodsStructure;
            }

        }

        public void SetType(LobbyType type)
        {
            if (MethodsPtr != IntPtr.Zero)
            {
                var res = Methods.SetType(MethodsPtr, type);
                if (res != Result.Ok)
                {
                    throw new ResultException(res);
                }
            }
        }

        public void SetOwner(int ownerId)
        {
            if (MethodsPtr != IntPtr.Zero)
            {
                var res = Methods.SetOwner(MethodsPtr, ownerId);
                if (res != Result.Ok)
                {
                    throw new ResultException(res);
                }
            }
        }

        public void SetCapacity(long capacity)
        {
            if (MethodsPtr != IntPtr.Zero)
            {
                var res = Methods.SetCapacity(MethodsPtr, capacity);
                if (res != Result.Ok)
                {
                    throw new ResultException(res);
                }
            }
        }

        public void SetMetadata(String key, String value)
        {
            if (MethodsPtr != IntPtr.Zero)
            {
                var res = Methods.SetMetadata(MethodsPtr, key, value);
                if (res != Result.Ok)
                {
                    throw new ResultException(res);
                }
            }
        }

        public void DeleteMetadata(String key)
        {
            if (MethodsPtr != IntPtr.Zero)
            {
                var res = Methods.DeleteMetadata(MethodsPtr, key);
                if (res != Result.Ok)
                {
                    throw new ResultException(res);
                }
            }
        }

        public void SetLocked(boolean locked)
        {
            if (MethodsPtr != IntPtr.Zero)
            {
                var res = Methods.SetLocked(MethodsPtr, locked);
                if (res != Result.Ok)
                {
                    throw new ResultException(res);
                }
            }
        }
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
    public partial struct LobbyMemberTransaction
    {
        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIMethods
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result SetMetadataMethod(IntPtr methodsPtr, [MarshalAs(UnmanagedType.LPStr)]String key, [MarshalAs(UnmanagedType.LPStr)]String value);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result DeleteMetadataMethod(IntPtr methodsPtr, [MarshalAs(UnmanagedType.LPStr)]String key);

            internal SetMetadataMethod SetMetadata;

            internal DeleteMetadataMethod DeleteMetadata;
        }

        internal IntPtr MethodsPtr;

        internal Object MethodsStructure;

        private FFIMethods Methods
        {
            get
            {
                if (MethodsStructure == null)
                {
                    MethodsStructure = Marshal.PtrToStructure(MethodsPtr, typeof(FFIMethods));
                }
                return (FFIMethods)MethodsStructure;
            }

        }

        public void SetMetadata(String key, String value)
        {
            if (MethodsPtr != IntPtr.Zero)
            {
                var res = Methods.SetMetadata(MethodsPtr, key, value);
                if (res != Result.Ok)
                {
                    throw new ResultException(res);
                }
            }
        }

        public void DeleteMetadata(String key)
        {
            if (MethodsPtr != IntPtr.Zero)
            {
                var res = Methods.DeleteMetadata(MethodsPtr, key);
                if (res != Result.Ok)
                {
                    throw new ResultException(res);
                }
            }
        }
    }

    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi)]
    public partial struct LobbySearchQuery
    {
        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIMethods
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result FilterMethod(IntPtr methodsPtr, [MarshalAs(UnmanagedType.LPStr)]String key, LobbySearchComparison comparison, LobbySearchCast cast, [MarshalAs(UnmanagedType.LPStr)]String value);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result SortMethod(IntPtr methodsPtr, [MarshalAs(UnmanagedType.LPStr)]String key, LobbySearchCast cast, [MarshalAs(UnmanagedType.LPStr)]String value);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result LimitMethod(IntPtr methodsPtr, long limit);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result DistanceMethod(IntPtr methodsPtr, LobbySearchDistance distance);

            internal FilterMethod Filter;

            internal SortMethod Sort;

            internal LimitMethod Limit;

            internal DistanceMethod Distance;
        }

        internal IntPtr MethodsPtr;

        internal Object MethodsStructure;

        private FFIMethods Methods
        {
            get
            {
                if (MethodsStructure == null)
                {
                    MethodsStructure = Marshal.PtrToStructure(MethodsPtr, typeof(FFIMethods));
                }
                return (FFIMethods)MethodsStructure;
            }

        }

        public void Filter(String key, LobbySearchComparison comparison, LobbySearchCast cast, String value)
        {
            if (MethodsPtr != IntPtr.Zero)
            {
                var res = Methods.Filter(MethodsPtr, key, comparison, cast, value);
                if (res != Result.Ok)
                {
                    throw new ResultException(res);
                }
            }
        }

        public void Sort(String key, LobbySearchCast cast, String value)
        {
            if (MethodsPtr != IntPtr.Zero)
            {
                var res = Methods.Sort(MethodsPtr, key, cast, value);
                if (res != Result.Ok)
                {
                    throw new ResultException(res);
                }
            }
        }

        public void Limit(long limit)
        {
            if (MethodsPtr != IntPtr.Zero)
            {
                var res = Methods.Limit(MethodsPtr, limit);
                if (res != Result.Ok)
                {
                    throw new ResultException(res);
                }
            }
        }

        public void Distance(LobbySearchDistance distance)
        {
            if (MethodsPtr != IntPtr.Zero)
            {
                var res = Methods.Distance(MethodsPtr, distance);
                if (res != Result.Ok)
                {
                    throw new ResultException(res);
                }
            }
        }
    }

    public partial class ResultException : Exception
    {
        public readonly Result Result;

        public ResultException(Result result) : base(result.ToString())
        {
        }
    }

    public partial class Discord : IDisposable
    {
        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIEvents
        {

        }

        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIMethods
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void DestroyHandler(IntPtr MethodsPtr);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result RunCallbacksMethod(IntPtr methodsPtr);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void SetLogHookCallback(IntPtr ptr, LogLevel level, [MarshalAs(UnmanagedType.LPStr)]String message);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void SetLogHookMethod(IntPtr methodsPtr, LogLevel minLevel, IntPtr callbackData, SetLogHookCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate IntPtr GetApplicationManagerMethod(IntPtr discordPtr);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate IntPtr GetUserManagerMethod(IntPtr discordPtr);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate IntPtr GetImageManagerMethod(IntPtr discordPtr);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate IntPtr GetActivityManagerMethod(IntPtr discordPtr);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate IntPtr GetRelationshipManagerMethod(IntPtr discordPtr);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate IntPtr GetLobbyManagerMethod(IntPtr discordPtr);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate IntPtr GetNetworkManagerMethod(IntPtr discordPtr);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate IntPtr GetOverlayManagerMethod(IntPtr discordPtr);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate IntPtr GetStorageManagerMethod(IntPtr discordPtr);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate IntPtr GetStoreManagerMethod(IntPtr discordPtr);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate IntPtr GetVoiceManagerMethod(IntPtr discordPtr);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate IntPtr GetAchievementManagerMethod(IntPtr discordPtr);

            internal DestroyHandler Destroy;

            internal RunCallbacksMethod RunCallbacks;

            internal SetLogHookMethod SetLogHook;

            internal GetApplicationManagerMethod GetApplicationManager;

            internal GetUserManagerMethod GetUserManager;

            internal GetImageManagerMethod GetImageManager;

            internal GetActivityManagerMethod GetActivityManager;

            internal GetRelationshipManagerMethod GetRelationshipManager;

            internal GetLobbyManagerMethod GetLobbyManager;

            internal GetNetworkManagerMethod GetNetworkManager;

            internal GetOverlayManagerMethod GetOverlayManager;

            internal GetStorageManagerMethod GetStorageManager;

            internal GetStoreManagerMethod GetStoreManager;

            internal GetVoiceManagerMethod GetVoiceManager;

            internal GetAchievementManagerMethod GetAchievementManager;
        }

        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFICreateParams
        {
            internal int ClientId;

            internal long Flags;

            internal IntPtr Events;

            internal IntPtr EventData;

            internal IntPtr ApplicationEvents;

            internal long ApplicationVersion;

            internal IntPtr UserEvents;

            internal long UserVersion;

            internal IntPtr ImageEvents;

            internal long ImageVersion;

            internal IntPtr ActivityEvents;

            internal long ActivityVersion;

            internal IntPtr RelationshipEvents;

            internal long RelationshipVersion;

            internal IntPtr LobbyEvents;

            internal long LobbyVersion;

            internal IntPtr NetworkEvents;

            internal long NetworkVersion;

            internal IntPtr OverlayEvents;

            internal long OverlayVersion;

            internal IntPtr StorageEvents;

            internal long StorageVersion;

            internal IntPtr StoreEvents;

            internal long StoreVersion;

            internal IntPtr VoiceEvents;

            internal long VoiceVersion;

            internal IntPtr AchievementEvents;

            internal long AchievementVersion;
        }

        [DllImport(Constants.DllName, ExactSpelling = true, CallingConvention = CallingConvention.Cdecl)]
        private static extern Result DiscordCreate(long version, ref FFICreateParams createParams, out IntPtr manager);

        public delegate void SetLogHookHandler(LogLevel level, String message);

        private GCHandle SelfHandle;

        private IntPtr EventsPtr;

        private FFIEvents Events;

        private IntPtr ApplicationEventsPtr;

        private ApplicationManager.FFIEvents ApplicationEvents;

        internal ApplicationManager ApplicationManagerInstance;

        private IntPtr UserEventsPtr;

        private UserManager.FFIEvents UserEvents;

        internal UserManager UserManagerInstance;

        private IntPtr ImageEventsPtr;

        private ImageManager.FFIEvents ImageEvents;

        internal ImageManager ImageManagerInstance;

        private IntPtr ActivityEventsPtr;

        private ActivityManager.FFIEvents ActivityEvents;

        internal ActivityManager ActivityManagerInstance;

        private IntPtr RelationshipEventsPtr;

        private RelationshipManager.FFIEvents RelationshipEvents;

        internal RelationshipManager RelationshipManagerInstance;

        private IntPtr LobbyEventsPtr;

        private LobbyManager.FFIEvents LobbyEvents;

        internal LobbyManager LobbyManagerInstance;

        private IntPtr NetworkEventsPtr;

        private NetworkManager.FFIEvents NetworkEvents;

        internal NetworkManager NetworkManagerInstance;

        private IntPtr OverlayEventsPtr;

        private OverlayManager.FFIEvents OverlayEvents;

        internal OverlayManager OverlayManagerInstance;

        private IntPtr StorageEventsPtr;

        private StorageManager.FFIEvents StorageEvents;

        internal StorageManager StorageManagerInstance;

        private IntPtr StoreEventsPtr;

        private StoreManager.FFIEvents StoreEvents;

        internal StoreManager StoreManagerInstance;

        private IntPtr VoiceEventsPtr;

        private VoiceManager.FFIEvents VoiceEvents;

        internal VoiceManager VoiceManagerInstance;

        private IntPtr AchievementEventsPtr;

        private AchievementManager.FFIEvents AchievementEvents;

        internal AchievementManager AchievementManagerInstance;

        private IntPtr MethodsPtr;

        private Object MethodsStructure;

        private FFIMethods Methods
        {
            get
            {
                if (MethodsStructure == null)
                {
                    MethodsStructure = Marshal.PtrToStructure(MethodsPtr, typeof(FFIMethods));
                }
                return (FFIMethods)MethodsStructure;
            }

        }

        private GCHandle? setLogHook;

        public Discord(int clientId, long flags)
        {
            FFICreateParams createParams;
            createParams.ClientId = clientId;
            createParams.Flags = flags;
            Events = new FFIEvents();
            EventsPtr = Marshal.AllocHGlobal(Marshal.SizeOf(Events));
            createParams.Events = EventsPtr;
            SelfHandle = GCHandle.Alloc(this);
            createParams.EventData = GCHandle.ToIntPtr(SelfHandle);
            ApplicationEvents = new ApplicationManager.FFIEvents();
            ApplicationEventsPtr = Marshal.AllocHGlobal(Marshal.SizeOf(ApplicationEvents));
            createParams.ApplicationEvents = ApplicationEventsPtr;
            createParams.ApplicationVersion = 1;
            UserEvents = new UserManager.FFIEvents();
            UserEventsPtr = Marshal.AllocHGlobal(Marshal.SizeOf(UserEvents));
            createParams.UserEvents = UserEventsPtr;
            createParams.UserVersion = 1;
            ImageEvents = new ImageManager.FFIEvents();
            ImageEventsPtr = Marshal.AllocHGlobal(Marshal.SizeOf(ImageEvents));
            createParams.ImageEvents = ImageEventsPtr;
            createParams.ImageVersion = 1;
            ActivityEvents = new ActivityManager.FFIEvents();
            ActivityEventsPtr = Marshal.AllocHGlobal(Marshal.SizeOf(ActivityEvents));
            createParams.ActivityEvents = ActivityEventsPtr;
            createParams.ActivityVersion = 1;
            RelationshipEvents = new RelationshipManager.FFIEvents();
            RelationshipEventsPtr = Marshal.AllocHGlobal(Marshal.SizeOf(RelationshipEvents));
            createParams.RelationshipEvents = RelationshipEventsPtr;
            createParams.RelationshipVersion = 1;
            LobbyEvents = new LobbyManager.FFIEvents();
            LobbyEventsPtr = Marshal.AllocHGlobal(Marshal.SizeOf(LobbyEvents));
            createParams.LobbyEvents = LobbyEventsPtr;
            createParams.LobbyVersion = 1;
            NetworkEvents = new NetworkManager.FFIEvents();
            NetworkEventsPtr = Marshal.AllocHGlobal(Marshal.SizeOf(NetworkEvents));
            createParams.NetworkEvents = NetworkEventsPtr;
            createParams.NetworkVersion = 1;
            OverlayEvents = new OverlayManager.FFIEvents();
            OverlayEventsPtr = Marshal.AllocHGlobal(Marshal.SizeOf(OverlayEvents));
            createParams.OverlayEvents = OverlayEventsPtr;
            createParams.OverlayVersion = 1;
            StorageEvents = new StorageManager.FFIEvents();
            StorageEventsPtr = Marshal.AllocHGlobal(Marshal.SizeOf(StorageEvents));
            createParams.StorageEvents = StorageEventsPtr;
            createParams.StorageVersion = 1;
            StoreEvents = new StoreManager.FFIEvents();
            StoreEventsPtr = Marshal.AllocHGlobal(Marshal.SizeOf(StoreEvents));
            createParams.StoreEvents = StoreEventsPtr;
            createParams.StoreVersion = 1;
            VoiceEvents = new VoiceManager.FFIEvents();
            VoiceEventsPtr = Marshal.AllocHGlobal(Marshal.SizeOf(VoiceEvents));
            createParams.VoiceEvents = VoiceEventsPtr;
            createParams.VoiceVersion = 1;
            AchievementEvents = new AchievementManager.FFIEvents();
            AchievementEventsPtr = Marshal.AllocHGlobal(Marshal.SizeOf(AchievementEvents));
            createParams.AchievementEvents = AchievementEventsPtr;
            createParams.AchievementVersion = 1;
            InitEvents(EventsPtr, ref Events);
            var result = DiscordCreate(2, ref createParams, out MethodsPtr);
            if (result != Result.Ok)
            {
                Dispose();
                throw new ResultException(result);
            }
        }

        private void InitEvents(IntPtr eventsPtr, ref FFIEvents events)
        {
            Marshal.StructureToPtr(events, eventsPtr, false);
        }

        public void Dispose()
        {
            if (MethodsPtr != IntPtr.Zero)
            {
                Methods.Destroy(MethodsPtr);
            }
            SelfHandle.Free();
            Marshal.FreeHGlobal(EventsPtr);
            Marshal.FreeHGlobal(ApplicationEventsPtr);
            Marshal.FreeHGlobal(UserEventsPtr);
            Marshal.FreeHGlobal(ImageEventsPtr);
            Marshal.FreeHGlobal(ActivityEventsPtr);
            Marshal.FreeHGlobal(RelationshipEventsPtr);
            Marshal.FreeHGlobal(LobbyEventsPtr);
            Marshal.FreeHGlobal(NetworkEventsPtr);
            Marshal.FreeHGlobal(OverlayEventsPtr);
            Marshal.FreeHGlobal(StorageEventsPtr);
            Marshal.FreeHGlobal(StoreEventsPtr);
            Marshal.FreeHGlobal(VoiceEventsPtr);
            Marshal.FreeHGlobal(AchievementEventsPtr);
            if (setLogHook.HasValue) {
               setLogHook.Value.Free();
            }
        }

        public void RunCallbacks()
        {
            var res = Methods.RunCallbacks(MethodsPtr);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
        }

        [MonoPInvokeCallback]
        private static void SetLogHookCallbackImpl(IntPtr ptr, LogLevel level, String message)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            SetLogHookHandler callback = (SetLogHookHandler)h.Target;
            callback(level, message);
        }

        public void SetLogHook(LogLevel minLevel, SetLogHookHandler callback)
        {
            if (setLogHook.HasValue) {
               setLogHook.Value.Free();
            }
             setLogHook = GCHandle.Alloc(callback);
            Methods.SetLogHook(MethodsPtr, minLevel, GCHandle.ToIntPtr(setLogHook.Value), SetLogHookCallbackImpl);
        }

        public ApplicationManager GetApplicationManager()
        {
            if (ApplicationManagerInstance == null) {
                ApplicationManagerInstance = new ApplicationManager(
                  Methods.GetApplicationManager(MethodsPtr),
                  ApplicationEventsPtr,
                  ref ApplicationEvents
                );
            }
            return ApplicationManagerInstance;
        }

        public UserManager GetUserManager()
        {
            if (UserManagerInstance == null) {
                UserManagerInstance = new UserManager(
                  Methods.GetUserManager(MethodsPtr),
                  UserEventsPtr,
                  ref UserEvents
                );
            }
            return UserManagerInstance;
        }

        public ImageManager GetImageManager()
        {
            if (ImageManagerInstance == null) {
                ImageManagerInstance = new ImageManager(
                  Methods.GetImageManager(MethodsPtr),
                  ImageEventsPtr,
                  ref ImageEvents
                );
            }
            return ImageManagerInstance;
        }

        public ActivityManager GetActivityManager()
        {
            if (ActivityManagerInstance == null) {
                ActivityManagerInstance = new ActivityManager(
                  Methods.GetActivityManager(MethodsPtr),
                  ActivityEventsPtr,
                  ref ActivityEvents
                );
            }
            return ActivityManagerInstance;
        }

        public RelationshipManager GetRelationshipManager()
        {
            if (RelationshipManagerInstance == null) {
                RelationshipManagerInstance = new RelationshipManager(
                  Methods.GetRelationshipManager(MethodsPtr),
                  RelationshipEventsPtr,
                  ref RelationshipEvents
                );
            }
            return RelationshipManagerInstance;
        }

        public LobbyManager GetLobbyManager()
        {
            if (LobbyManagerInstance == null) {
                LobbyManagerInstance = new LobbyManager(
                  Methods.GetLobbyManager(MethodsPtr),
                  LobbyEventsPtr,
                  ref LobbyEvents
                );
            }
            return LobbyManagerInstance;
        }

        public NetworkManager GetNetworkManager()
        {
            if (NetworkManagerInstance == null) {
                NetworkManagerInstance = new NetworkManager(
                  Methods.GetNetworkManager(MethodsPtr),
                  NetworkEventsPtr,
                  ref NetworkEvents
                );
            }
            return NetworkManagerInstance;
        }

        public OverlayManager GetOverlayManager()
        {
            if (OverlayManagerInstance == null) {
                OverlayManagerInstance = new OverlayManager(
                  Methods.GetOverlayManager(MethodsPtr),
                  OverlayEventsPtr,
                  ref OverlayEvents
                );
            }
            return OverlayManagerInstance;
        }

        public StorageManager GetStorageManager()
        {
            if (StorageManagerInstance == null) {
                StorageManagerInstance = new StorageManager(
                  Methods.GetStorageManager(MethodsPtr),
                  StorageEventsPtr,
                  ref StorageEvents
                );
            }
            return StorageManagerInstance;
        }

        public StoreManager GetStoreManager()
        {
            if (StoreManagerInstance == null) {
                StoreManagerInstance = new StoreManager(
                  Methods.GetStoreManager(MethodsPtr),
                  StoreEventsPtr,
                  ref StoreEvents
                );
            }
            return StoreManagerInstance;
        }

        public VoiceManager GetVoiceManager()
        {
            if (VoiceManagerInstance == null) {
                VoiceManagerInstance = new VoiceManager(
                  Methods.GetVoiceManager(MethodsPtr),
                  VoiceEventsPtr,
                  ref VoiceEvents
                );
            }
            return VoiceManagerInstance;
        }

        public AchievementManager GetAchievementManager()
        {
            if (AchievementManagerInstance == null) {
                AchievementManagerInstance = new AchievementManager(
                  Methods.GetAchievementManager(MethodsPtr),
                  AchievementEventsPtr,
                  ref AchievementEvents
                );
            }
            return AchievementManagerInstance;
        }
    }

    internal partial class MonoPInvokeCallbackAttribute : Attribute
    {

    }

    public partial class ApplicationManager
    {
        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIEvents
        {

        }

        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIMethods
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void ValidateOrExitCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void ValidateOrExitMethod(IntPtr methodsPtr, IntPtr callbackData, ValidateOrExitCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void GetCurrentLocaleMethod(IntPtr methodsPtr, StringBuilder locale);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void GetCurrentBranchMethod(IntPtr methodsPtr, StringBuilder branch);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void GetOAuth2TokenCallback(IntPtr ptr, Result result, ref OAuth2Token oauth2Token);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void GetOAuth2TokenMethod(IntPtr methodsPtr, IntPtr callbackData, GetOAuth2TokenCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void GetTicketCallback(IntPtr ptr, Result result, [MarshalAs(UnmanagedType.LPStr)]ref String data);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void GetTicketMethod(IntPtr methodsPtr, IntPtr callbackData, GetTicketCallback callback);

            internal ValidateOrExitMethod ValidateOrExit;

            internal GetCurrentLocaleMethod GetCurrentLocale;

            internal GetCurrentBranchMethod GetCurrentBranch;

            internal GetOAuth2TokenMethod GetOAuth2Token;

            internal GetTicketMethod GetTicket;
        }

        public delegate void ValidateOrExitHandler(Result result);

        public delegate void GetOAuth2TokenHandler(Result result, ref OAuth2Token oauth2Token);

        public delegate void GetTicketHandler(Result result, ref String data);

        private IntPtr MethodsPtr;

        private Object MethodsStructure;

        private FFIMethods Methods
        {
            get
            {
                if (MethodsStructure == null)
                {
                    MethodsStructure = Marshal.PtrToStructure(MethodsPtr, typeof(FFIMethods));
                }
                return (FFIMethods)MethodsStructure;
            }

        }

        internal ApplicationManager(IntPtr ptr, IntPtr eventsPtr, ref FFIEvents events)
        {
            if (eventsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
            InitEvents(eventsPtr, ref events);
            MethodsPtr = ptr;
            if (MethodsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
        }

        private void InitEvents(IntPtr eventsPtr, ref FFIEvents events)
        {
            Marshal.StructureToPtr(events, eventsPtr, false);
        }

        [MonoPInvokeCallback]
        private static void ValidateOrExitCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            ValidateOrExitHandler callback = (ValidateOrExitHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void ValidateOrExit(ValidateOrExitHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.ValidateOrExit(MethodsPtr, GCHandle.ToIntPtr(wrapped), ValidateOrExitCallbackImpl);
        }

        public String GetCurrentLocale()
        {
            var ret = new StringBuilder(128);
            Methods.GetCurrentLocale(MethodsPtr, ret);
            return ret.ToString();
        }

        public String GetCurrentBranch()
        {
            var ret = new StringBuilder(4096);
            Methods.GetCurrentBranch(MethodsPtr, ret);
            return ret.ToString();
        }

        [MonoPInvokeCallback]
        private static void GetOAuth2TokenCallbackImpl(IntPtr ptr, Result result, ref OAuth2Token oauth2Token)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            GetOAuth2TokenHandler callback = (GetOAuth2TokenHandler)h.Target;
            h.Free();
            callback(result, ref oauth2Token);
        }

        public void GetOAuth2Token(GetOAuth2TokenHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.GetOAuth2Token(MethodsPtr, GCHandle.ToIntPtr(wrapped), GetOAuth2TokenCallbackImpl);
        }

        [MonoPInvokeCallback]
        private static void GetTicketCallbackImpl(IntPtr ptr, Result result, ref String data)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            GetTicketHandler callback = (GetTicketHandler)h.Target;
            h.Free();
            callback(result, ref data);
        }

        public void GetTicket(GetTicketHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.GetTicket(MethodsPtr, GCHandle.ToIntPtr(wrapped), GetTicketCallbackImpl);
        }
    }

    public partial class UserManager
    {
        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIEvents
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void CurrentUserUpdateHandler(IntPtr ptr);

            internal CurrentUserUpdateHandler OnCurrentUserUpdate;
        }

        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIMethods
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetCurrentUserMethod(IntPtr methodsPtr, ref User currentUser);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void GetUserCallback(IntPtr ptr, Result result, ref User user);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void GetUserMethod(IntPtr methodsPtr, int userId, IntPtr callbackData, GetUserCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetCurrentUserPremiumTypeMethod(IntPtr methodsPtr, ref PremiumType premiumType);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result CurrentUserHasFlagMethod(IntPtr methodsPtr, UserFlag flag, ref boolean hasFlag);

            internal GetCurrentUserMethod GetCurrentUser;

            internal GetUserMethod GetUser;

            internal GetCurrentUserPremiumTypeMethod GetCurrentUserPremiumType;

            internal CurrentUserHasFlagMethod CurrentUserHasFlag;
        }

        public delegate void GetUserHandler(Result result, ref User user);

        public delegate void CurrentUserUpdateHandler();

        private IntPtr MethodsPtr;

        private Object MethodsStructure;

        private FFIMethods Methods
        {
            get
            {
                if (MethodsStructure == null)
                {
                    MethodsStructure = Marshal.PtrToStructure(MethodsPtr, typeof(FFIMethods));
                }
                return (FFIMethods)MethodsStructure;
            }

        }

        public event CurrentUserUpdateHandler OnCurrentUserUpdate;

        internal UserManager(IntPtr ptr, IntPtr eventsPtr, ref FFIEvents events)
        {
            if (eventsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
            InitEvents(eventsPtr, ref events);
            MethodsPtr = ptr;
            if (MethodsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
        }

        private void InitEvents(IntPtr eventsPtr, ref FFIEvents events)
        {
            events.OnCurrentUserUpdate = OnCurrentUserUpdateImpl;
            Marshal.StructureToPtr(events, eventsPtr, false);
        }

        public User GetCurrentUser()
        {
            var ret = new User();
            var res = Methods.GetCurrentUser(MethodsPtr, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        [MonoPInvokeCallback]
        private static void GetUserCallbackImpl(IntPtr ptr, Result result, ref User user)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            GetUserHandler callback = (GetUserHandler)h.Target;
            h.Free();
            callback(result, ref user);
        }

        public void GetUser(int userId, GetUserHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.GetUser(MethodsPtr, userId, GCHandle.ToIntPtr(wrapped), GetUserCallbackImpl);
        }

        public PremiumType GetCurrentUserPremiumType()
        {
            var ret = new PremiumType();
            var res = Methods.GetCurrentUserPremiumType(MethodsPtr, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        public boolean CurrentUserHasFlag(UserFlag flag)
        {
            var ret = new boolean();
            var res = Methods.CurrentUserHasFlag(MethodsPtr, flag, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        [MonoPInvokeCallback]
        private static void OnCurrentUserUpdateImpl(IntPtr ptr)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            Discord d = (Discord)h.Target;
            if (d.UserManagerInstance.OnCurrentUserUpdate != null)
            {
                d.UserManagerInstance.OnCurrentUserUpdate.Invoke();
            }
        }
    }

    public partial class ImageManager
    {
        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIEvents
        {

        }

        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIMethods
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void FetchCallback(IntPtr ptr, Result result, ImageHandle handleResult);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void FetchMethod(IntPtr methodsPtr, ImageHandle handle, boolean refresh, IntPtr callbackData, FetchCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetDimensionsMethod(IntPtr methodsPtr, ImageHandle handle, ref ImageDimensions dimensions);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetDataMethod(IntPtr methodsPtr, ImageHandle handle, byte[] data, Int32 dataLen);

            internal FetchMethod Fetch;

            internal GetDimensionsMethod GetDimensions;

            internal GetDataMethod GetData;
        }

        public delegate void FetchHandler(Result result, ImageHandle handleResult);

        private IntPtr MethodsPtr;

        private Object MethodsStructure;

        private FFIMethods Methods
        {
            get
            {
                if (MethodsStructure == null)
                {
                    MethodsStructure = Marshal.PtrToStructure(MethodsPtr, typeof(FFIMethods));
                }
                return (FFIMethods)MethodsStructure;
            }

        }

        internal ImageManager(IntPtr ptr, IntPtr eventsPtr, ref FFIEvents events)
        {
            if (eventsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
            InitEvents(eventsPtr, ref events);
            MethodsPtr = ptr;
            if (MethodsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
        }

        private void InitEvents(IntPtr eventsPtr, ref FFIEvents events)
        {
            Marshal.StructureToPtr(events, eventsPtr, false);
        }

        [MonoPInvokeCallback]
        private static void FetchCallbackImpl(IntPtr ptr, Result result, ImageHandle handleResult)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            FetchHandler callback = (FetchHandler)h.Target;
            h.Free();
            callback(result, handleResult);
        }

        public void Fetch(ImageHandle handle, boolean refresh, FetchHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.Fetch(MethodsPtr, handle, refresh, GCHandle.ToIntPtr(wrapped), FetchCallbackImpl);
        }

        public ImageDimensions GetDimensions(ImageHandle handle)
        {
            var ret = new ImageDimensions();
            var res = Methods.GetDimensions(MethodsPtr, handle, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        public void GetData(ImageHandle handle, byte[] data)
        {
            var res = Methods.GetData(MethodsPtr, handle, data, data.Length);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
        }
    }

    public partial class ActivityManager
    {
        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIEvents
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void ActivityJoinHandler(IntPtr ptr, [MarshalAs(UnmanagedType.LPStr)]String secret);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void ActivitySpectateHandler(IntPtr ptr, [MarshalAs(UnmanagedType.LPStr)]String secret);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void ActivityJoinRequestHandler(IntPtr ptr, ref User user);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void ActivityInviteHandler(IntPtr ptr, ActivityActionType type, ref User user, ref Activity activity);

            internal ActivityJoinHandler OnActivityJoin;

            internal ActivitySpectateHandler OnActivitySpectate;

            internal ActivityJoinRequestHandler OnActivityJoinRequest;

            internal ActivityInviteHandler OnActivityInvite;
        }

        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIMethods
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result RegisterCommandMethod(IntPtr methodsPtr, [MarshalAs(UnmanagedType.LPStr)]String command);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result RegisterSteamMethod(IntPtr methodsPtr, long steamId);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void UpdateActivityCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void UpdateActivityMethod(IntPtr methodsPtr, ref Activity activity, IntPtr callbackData, UpdateActivityCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void ClearActivityCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void ClearActivityMethod(IntPtr methodsPtr, IntPtr callbackData, ClearActivityCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void SendRequestReplyCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void SendRequestReplyMethod(IntPtr methodsPtr, int userId, ActivityJoinRequestReply reply, IntPtr callbackData, SendRequestReplyCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void SendInviteCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void SendInviteMethod(IntPtr methodsPtr, int userId, ActivityActionType type, [MarshalAs(UnmanagedType.LPStr)]String content, IntPtr callbackData, SendInviteCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void AcceptInviteCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void AcceptInviteMethod(IntPtr methodsPtr, int userId, IntPtr callbackData, AcceptInviteCallback callback);

            internal RegisterCommandMethod RegisterCommand;

            internal RegisterSteamMethod RegisterSteam;

            internal UpdateActivityMethod UpdateActivity;

            internal ClearActivityMethod ClearActivity;

            internal SendRequestReplyMethod SendRequestReply;

            internal SendInviteMethod SendInvite;

            internal AcceptInviteMethod AcceptInvite;
        }

        public delegate void UpdateActivityHandler(Result result);

        public delegate void ClearActivityHandler(Result result);

        public delegate void SendRequestReplyHandler(Result result);

        public delegate void SendInviteHandler(Result result);

        public delegate void AcceptInviteHandler(Result result);

        public delegate void ActivityJoinHandler(String secret);

        public delegate void ActivitySpectateHandler(String secret);

        public delegate void ActivityJoinRequestHandler(ref User user);

        public delegate void ActivityInviteHandler(ActivityActionType type, ref User user, ref Activity activity);

        private IntPtr MethodsPtr;

        private Object MethodsStructure;

        private FFIMethods Methods
        {
            get
            {
                if (MethodsStructure == null)
                {
                    MethodsStructure = Marshal.PtrToStructure(MethodsPtr, typeof(FFIMethods));
                }
                return (FFIMethods)MethodsStructure;
            }

        }

        public event ActivityJoinHandler OnActivityJoin;

        public event ActivitySpectateHandler OnActivitySpectate;

        public event ActivityJoinRequestHandler OnActivityJoinRequest;

        public event ActivityInviteHandler OnActivityInvite;

        internal ActivityManager(IntPtr ptr, IntPtr eventsPtr, ref FFIEvents events)
        {
            if (eventsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
            InitEvents(eventsPtr, ref events);
            MethodsPtr = ptr;
            if (MethodsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
        }

        private void InitEvents(IntPtr eventsPtr, ref FFIEvents events)
        {
            events.OnActivityJoin = OnActivityJoinImpl;
            events.OnActivitySpectate = OnActivitySpectateImpl;
            events.OnActivityJoinRequest = OnActivityJoinRequestImpl;
            events.OnActivityInvite = OnActivityInviteImpl;
            Marshal.StructureToPtr(events, eventsPtr, false);
        }

        public void RegisterCommand(String command)
        {
            var res = Methods.RegisterCommand(MethodsPtr, command);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
        }

        public void RegisterSteam(long steamId)
        {
            var res = Methods.RegisterSteam(MethodsPtr, steamId);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
        }

        [MonoPInvokeCallback]
        private static void UpdateActivityCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            UpdateActivityHandler callback = (UpdateActivityHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void UpdateActivity(Activity activity, UpdateActivityHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.UpdateActivity(MethodsPtr, ref activity, GCHandle.ToIntPtr(wrapped), UpdateActivityCallbackImpl);
        }

        [MonoPInvokeCallback]
        private static void ClearActivityCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            ClearActivityHandler callback = (ClearActivityHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void ClearActivity(ClearActivityHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.ClearActivity(MethodsPtr, GCHandle.ToIntPtr(wrapped), ClearActivityCallbackImpl);
        }

        [MonoPInvokeCallback]
        private static void SendRequestReplyCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            SendRequestReplyHandler callback = (SendRequestReplyHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void SendRequestReply(int userId, ActivityJoinRequestReply reply, SendRequestReplyHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.SendRequestReply(MethodsPtr, userId, reply, GCHandle.ToIntPtr(wrapped), SendRequestReplyCallbackImpl);
        }

        [MonoPInvokeCallback]
        private static void SendInviteCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            SendInviteHandler callback = (SendInviteHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void SendInvite(int userId, ActivityActionType type, String content, SendInviteHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.SendInvite(MethodsPtr, userId, type, content, GCHandle.ToIntPtr(wrapped), SendInviteCallbackImpl);
        }

        [MonoPInvokeCallback]
        private static void AcceptInviteCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            AcceptInviteHandler callback = (AcceptInviteHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void AcceptInvite(int userId, AcceptInviteHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.AcceptInvite(MethodsPtr, userId, GCHandle.ToIntPtr(wrapped), AcceptInviteCallbackImpl);
        }

        [MonoPInvokeCallback]
        private static void OnActivityJoinImpl(IntPtr ptr, String secret)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            Discord d = (Discord)h.Target;
            if (d.ActivityManagerInstance.OnActivityJoin != null)
            {
                d.ActivityManagerInstance.OnActivityJoin.Invoke(secret);
            }
        }

        [MonoPInvokeCallback]
        private static void OnActivitySpectateImpl(IntPtr ptr, String secret)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            Discord d = (Discord)h.Target;
            if (d.ActivityManagerInstance.OnActivitySpectate != null)
            {
                d.ActivityManagerInstance.OnActivitySpectate.Invoke(secret);
            }
        }

        [MonoPInvokeCallback]
        private static void OnActivityJoinRequestImpl(IntPtr ptr, ref User user)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            Discord d = (Discord)h.Target;
            if (d.ActivityManagerInstance.OnActivityJoinRequest != null)
            {
                d.ActivityManagerInstance.OnActivityJoinRequest.Invoke(ref user);
            }
        }

        [MonoPInvokeCallback]
        private static void OnActivityInviteImpl(IntPtr ptr, ActivityActionType type, ref User user, ref Activity activity)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            Discord d = (Discord)h.Target;
            if (d.ActivityManagerInstance.OnActivityInvite != null)
            {
                d.ActivityManagerInstance.OnActivityInvite.Invoke(type, ref user, ref activity);
            }
        }
    }

    public partial class RelationshipManager
    {
        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIEvents
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void RefreshHandler(IntPtr ptr);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void RelationshipUpdateHandler(IntPtr ptr, ref Relationship relationship);

            internal RefreshHandler OnRefresh;

            internal RelationshipUpdateHandler OnRelationshipUpdate;
        }

        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIMethods
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate boolean FilterCallback(IntPtr ptr, ref Relationship relationship);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void FilterMethod(IntPtr methodsPtr, IntPtr callbackData, FilterCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result CountMethod(IntPtr methodsPtr, ref Int32 count);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetMethod(IntPtr methodsPtr, int userId, ref Relationship relationship);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetAtMethod(IntPtr methodsPtr, long index, ref Relationship relationship);

            internal FilterMethod Filter;

            internal CountMethod Count;

            internal GetMethod Get;

            internal GetAtMethod GetAt;
        }

        public delegate boolean FilterHandler(ref Relationship relationship);

        public delegate void RefreshHandler();

        public delegate void RelationshipUpdateHandler(ref Relationship relationship);

        private IntPtr MethodsPtr;

        private Object MethodsStructure;

        private FFIMethods Methods
        {
            get
            {
                if (MethodsStructure == null)
                {
                    MethodsStructure = Marshal.PtrToStructure(MethodsPtr, typeof(FFIMethods));
                }
                return (FFIMethods)MethodsStructure;
            }

        }

        public event RefreshHandler OnRefresh;

        public event RelationshipUpdateHandler OnRelationshipUpdate;

        internal RelationshipManager(IntPtr ptr, IntPtr eventsPtr, ref FFIEvents events)
        {
            if (eventsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
            InitEvents(eventsPtr, ref events);
            MethodsPtr = ptr;
            if (MethodsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
        }

        private void InitEvents(IntPtr eventsPtr, ref FFIEvents events)
        {
            events.OnRefresh = OnRefreshImpl;
            events.OnRelationshipUpdate = OnRelationshipUpdateImpl;
            Marshal.StructureToPtr(events, eventsPtr, false);
        }

        [MonoPInvokeCallback]
        private static boolean FilterCallbackImpl(IntPtr ptr, ref Relationship relationship)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            FilterHandler callback = (FilterHandler)h.Target;
            return callback(ref relationship);
        }

        public void Filter(FilterHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.Filter(MethodsPtr, GCHandle.ToIntPtr(wrapped), FilterCallbackImpl);
            wrapped.Free();
        }

        public Int32 Count()
        {
            var ret = new Int32();
            var res = Methods.Count(MethodsPtr, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        public Relationship Get(int userId)
        {
            var ret = new Relationship();
            var res = Methods.Get(MethodsPtr, userId, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        public Relationship GetAt(long index)
        {
            var ret = new Relationship();
            var res = Methods.GetAt(MethodsPtr, index, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        [MonoPInvokeCallback]
        private static void OnRefreshImpl(IntPtr ptr)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            Discord d = (Discord)h.Target;
            if (d.RelationshipManagerInstance.OnRefresh != null)
            {
                d.RelationshipManagerInstance.OnRefresh.Invoke();
            }
        }

        [MonoPInvokeCallback]
        private static void OnRelationshipUpdateImpl(IntPtr ptr, ref Relationship relationship)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            Discord d = (Discord)h.Target;
            if (d.RelationshipManagerInstance.OnRelationshipUpdate != null)
            {
                d.RelationshipManagerInstance.OnRelationshipUpdate.Invoke(ref relationship);
            }
        }
    }

    public partial class LobbyManager
    {
        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIEvents
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void LobbyUpdateHandler(IntPtr ptr, int lobbyId);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void LobbyDeleteHandler(IntPtr ptr, int lobbyId, long reason);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void MemberConnectHandler(IntPtr ptr, int lobbyId, int userId);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void MemberUpdateHandler(IntPtr ptr, int lobbyId, int userId);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void MemberDisconnectHandler(IntPtr ptr, int lobbyId, int userId);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void LobbyMessageHandler(IntPtr ptr, int lobbyId, int userId, IntPtr dataPtr, Int32 dataLen);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void SpeakingHandler(IntPtr ptr, int lobbyId, int userId, boolean speaking);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void NetworkMessageHandler(IntPtr ptr, int lobbyId, int userId, byte channelId, IntPtr dataPtr, Int32 dataLen);

            internal LobbyUpdateHandler OnLobbyUpdate;

            internal LobbyDeleteHandler OnLobbyDelete;

            internal MemberConnectHandler OnMemberConnect;

            internal MemberUpdateHandler OnMemberUpdate;

            internal MemberDisconnectHandler OnMemberDisconnect;

            internal LobbyMessageHandler OnLobbyMessage;

            internal SpeakingHandler OnSpeaking;

            internal NetworkMessageHandler OnNetworkMessage;
        }

        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIMethods
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetLobbyCreateTransactionMethod(IntPtr methodsPtr, ref IntPtr transaction);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetLobbyUpdateTransactionMethod(IntPtr methodsPtr, int lobbyId, ref IntPtr transaction);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetMemberUpdateTransactionMethod(IntPtr methodsPtr, int lobbyId, int userId, ref IntPtr transaction);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void CreateLobbyCallback(IntPtr ptr, Result result, ref Lobby lobby);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void CreateLobbyMethod(IntPtr methodsPtr, IntPtr transaction, IntPtr callbackData, CreateLobbyCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void UpdateLobbyCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void UpdateLobbyMethod(IntPtr methodsPtr, int lobbyId, IntPtr transaction, IntPtr callbackData, UpdateLobbyCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void DeleteLobbyCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void DeleteLobbyMethod(IntPtr methodsPtr, int lobbyId, IntPtr callbackData, DeleteLobbyCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void ConnectLobbyCallback(IntPtr ptr, Result result, ref Lobby lobby);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void ConnectLobbyMethod(IntPtr methodsPtr, int lobbyId, [MarshalAs(UnmanagedType.LPStr)]String secret, IntPtr callbackData, ConnectLobbyCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void ConnectLobbyWithActivitySecretCallback(IntPtr ptr, Result result, ref Lobby lobby);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void ConnectLobbyWithActivitySecretMethod(IntPtr methodsPtr, [MarshalAs(UnmanagedType.LPStr)]String activitySecret, IntPtr callbackData, ConnectLobbyWithActivitySecretCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void DisconnectLobbyCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void DisconnectLobbyMethod(IntPtr methodsPtr, int lobbyId, IntPtr callbackData, DisconnectLobbyCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetLobbyMethod(IntPtr methodsPtr, int lobbyId, ref Lobby lobby);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetLobbyActivitySecretMethod(IntPtr methodsPtr, int lobbyId, StringBuilder secret);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetLobbyMetadataValueMethod(IntPtr methodsPtr, int lobbyId, [MarshalAs(UnmanagedType.LPStr)]String key, StringBuilder value);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetLobbyMetadataKeyMethod(IntPtr methodsPtr, int lobbyId, Int32 index, StringBuilder key);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result LobbyMetadataCountMethod(IntPtr methodsPtr, int lobbyId, ref Int32 count);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result MemberCountMethod(IntPtr methodsPtr, int lobbyId, ref Int32 count);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetMemberUserIdMethod(IntPtr methodsPtr, int lobbyId, Int32 index, ref int userId);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetMemberUserMethod(IntPtr methodsPtr, int lobbyId, int userId, ref User user);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetMemberMetadataValueMethod(IntPtr methodsPtr, int lobbyId, int userId, [MarshalAs(UnmanagedType.LPStr)]String key, StringBuilder value);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetMemberMetadataKeyMethod(IntPtr methodsPtr, int lobbyId, int userId, Int32 index, StringBuilder key);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result MemberMetadataCountMethod(IntPtr methodsPtr, int lobbyId, int userId, ref Int32 count);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void UpdateMemberCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void UpdateMemberMethod(IntPtr methodsPtr, int lobbyId, int userId, IntPtr transaction, IntPtr callbackData, UpdateMemberCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void SendLobbyMessageCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void SendLobbyMessageMethod(IntPtr methodsPtr, int lobbyId, byte[] data, Int32 dataLen, IntPtr callbackData, SendLobbyMessageCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetSearchQueryMethod(IntPtr methodsPtr, ref IntPtr query);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void SearchCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void SearchMethod(IntPtr methodsPtr, IntPtr query, IntPtr callbackData, SearchCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void LobbyCountMethod(IntPtr methodsPtr, ref Int32 count);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetLobbyIdMethod(IntPtr methodsPtr, Int32 index, ref int lobbyId);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void ConnectVoiceCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void ConnectVoiceMethod(IntPtr methodsPtr, int lobbyId, IntPtr callbackData, ConnectVoiceCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void DisconnectVoiceCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void DisconnectVoiceMethod(IntPtr methodsPtr, int lobbyId, IntPtr callbackData, DisconnectVoiceCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result ConnectNetworkMethod(IntPtr methodsPtr, int lobbyId);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result DisconnectNetworkMethod(IntPtr methodsPtr, int lobbyId);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result FlushNetworkMethod(IntPtr methodsPtr);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result OpenNetworkChannelMethod(IntPtr methodsPtr, int lobbyId, byte channelId, boolean reliable);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result SendNetworkMessageMethod(IntPtr methodsPtr, int lobbyId, int userId, byte channelId, byte[] data, Int32 dataLen);

            internal GetLobbyCreateTransactionMethod GetLobbyCreateTransaction;

            internal GetLobbyUpdateTransactionMethod GetLobbyUpdateTransaction;

            internal GetMemberUpdateTransactionMethod GetMemberUpdateTransaction;

            internal CreateLobbyMethod CreateLobby;

            internal UpdateLobbyMethod UpdateLobby;

            internal DeleteLobbyMethod DeleteLobby;

            internal ConnectLobbyMethod ConnectLobby;

            internal ConnectLobbyWithActivitySecretMethod ConnectLobbyWithActivitySecret;

            internal DisconnectLobbyMethod DisconnectLobby;

            internal GetLobbyMethod GetLobby;

            internal GetLobbyActivitySecretMethod GetLobbyActivitySecret;

            internal GetLobbyMetadataValueMethod GetLobbyMetadataValue;

            internal GetLobbyMetadataKeyMethod GetLobbyMetadataKey;

            internal LobbyMetadataCountMethod LobbyMetadataCount;

            internal MemberCountMethod MemberCount;

            internal GetMemberUserIdMethod GetMemberUserId;

            internal GetMemberUserMethod GetMemberUser;

            internal GetMemberMetadataValueMethod GetMemberMetadataValue;

            internal GetMemberMetadataKeyMethod GetMemberMetadataKey;

            internal MemberMetadataCountMethod MemberMetadataCount;

            internal UpdateMemberMethod UpdateMember;

            internal SendLobbyMessageMethod SendLobbyMessage;

            internal GetSearchQueryMethod GetSearchQuery;

            internal SearchMethod Search;

            internal LobbyCountMethod LobbyCount;

            internal GetLobbyIdMethod GetLobbyId;

            internal ConnectVoiceMethod ConnectVoice;

            internal DisconnectVoiceMethod DisconnectVoice;

            internal ConnectNetworkMethod ConnectNetwork;

            internal DisconnectNetworkMethod DisconnectNetwork;

            internal FlushNetworkMethod FlushNetwork;

            internal OpenNetworkChannelMethod OpenNetworkChannel;

            internal SendNetworkMessageMethod SendNetworkMessage;
        }

        public delegate void CreateLobbyHandler(Result result, ref Lobby lobby);

        public delegate void UpdateLobbyHandler(Result result);

        public delegate void DeleteLobbyHandler(Result result);

        public delegate void ConnectLobbyHandler(Result result, ref Lobby lobby);

        public delegate void ConnectLobbyWithActivitySecretHandler(Result result, ref Lobby lobby);

        public delegate void DisconnectLobbyHandler(Result result);

        public delegate void UpdateMemberHandler(Result result);

        public delegate void SendLobbyMessageHandler(Result result);

        public delegate void SearchHandler(Result result);

        public delegate void ConnectVoiceHandler(Result result);

        public delegate void DisconnectVoiceHandler(Result result);

        public delegate void LobbyUpdateHandler(int lobbyId);

        public delegate void LobbyDeleteHandler(int lobbyId, long reason);

        public delegate void MemberConnectHandler(int lobbyId, int userId);

        public delegate void MemberUpdateHandler(int lobbyId, int userId);

        public delegate void MemberDisconnectHandler(int lobbyId, int userId);

        public delegate void LobbyMessageHandler(int lobbyId, int userId, byte[] data);

        public delegate void SpeakingHandler(int lobbyId, int userId, boolean speaking);

        public delegate void NetworkMessageHandler(int lobbyId, int userId, byte channelId, byte[] data);

        private IntPtr MethodsPtr;

        private Object MethodsStructure;

        private FFIMethods Methods
        {
            get
            {
                if (MethodsStructure == null)
                {
                    MethodsStructure = Marshal.PtrToStructure(MethodsPtr, typeof(FFIMethods));
                }
                return (FFIMethods)MethodsStructure;
            }

        }

        public event LobbyUpdateHandler OnLobbyUpdate;

        public event LobbyDeleteHandler OnLobbyDelete;

        public event MemberConnectHandler OnMemberConnect;

        public event MemberUpdateHandler OnMemberUpdate;

        public event MemberDisconnectHandler OnMemberDisconnect;

        public event LobbyMessageHandler OnLobbyMessage;

        public event SpeakingHandler OnSpeaking;

        public event NetworkMessageHandler OnNetworkMessage;

        internal LobbyManager(IntPtr ptr, IntPtr eventsPtr, ref FFIEvents events)
        {
            if (eventsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
            InitEvents(eventsPtr, ref events);
            MethodsPtr = ptr;
            if (MethodsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
        }

        private void InitEvents(IntPtr eventsPtr, ref FFIEvents events)
        {
            events.OnLobbyUpdate = OnLobbyUpdateImpl;
            events.OnLobbyDelete = OnLobbyDeleteImpl;
            events.OnMemberConnect = OnMemberConnectImpl;
            events.OnMemberUpdate = OnMemberUpdateImpl;
            events.OnMemberDisconnect = OnMemberDisconnectImpl;
            events.OnLobbyMessage = OnLobbyMessageImpl;
            events.OnSpeaking = OnSpeakingImpl;
            events.OnNetworkMessage = OnNetworkMessageImpl;
            Marshal.StructureToPtr(events, eventsPtr, false);
        }

        public LobbyTransaction GetLobbyCreateTransaction()
        {
            var ret = new LobbyTransaction();
            var res = Methods.GetLobbyCreateTransaction(MethodsPtr, ref ret.MethodsPtr);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        public LobbyTransaction GetLobbyUpdateTransaction(int lobbyId)
        {
            var ret = new LobbyTransaction();
            var res = Methods.GetLobbyUpdateTransaction(MethodsPtr, lobbyId, ref ret.MethodsPtr);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        public LobbyMemberTransaction GetMemberUpdateTransaction(int lobbyId, int userId)
        {
            var ret = new LobbyMemberTransaction();
            var res = Methods.GetMemberUpdateTransaction(MethodsPtr, lobbyId, userId, ref ret.MethodsPtr);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        [MonoPInvokeCallback]
        private static void CreateLobbyCallbackImpl(IntPtr ptr, Result result, ref Lobby lobby)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            CreateLobbyHandler callback = (CreateLobbyHandler)h.Target;
            h.Free();
            callback(result, ref lobby);
        }

        public void CreateLobby(LobbyTransaction transaction, CreateLobbyHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.CreateLobby(MethodsPtr, transaction.MethodsPtr, GCHandle.ToIntPtr(wrapped), CreateLobbyCallbackImpl);
            transaction.MethodsPtr = IntPtr.Zero;
        }

        [MonoPInvokeCallback]
        private static void UpdateLobbyCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            UpdateLobbyHandler callback = (UpdateLobbyHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void UpdateLobby(int lobbyId, LobbyTransaction transaction, UpdateLobbyHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.UpdateLobby(MethodsPtr, lobbyId, transaction.MethodsPtr, GCHandle.ToIntPtr(wrapped), UpdateLobbyCallbackImpl);
            transaction.MethodsPtr = IntPtr.Zero;
        }

        [MonoPInvokeCallback]
        private static void DeleteLobbyCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            DeleteLobbyHandler callback = (DeleteLobbyHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void DeleteLobby(int lobbyId, DeleteLobbyHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.DeleteLobby(MethodsPtr, lobbyId, GCHandle.ToIntPtr(wrapped), DeleteLobbyCallbackImpl);
        }

        [MonoPInvokeCallback]
        private static void ConnectLobbyCallbackImpl(IntPtr ptr, Result result, ref Lobby lobby)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            ConnectLobbyHandler callback = (ConnectLobbyHandler)h.Target;
            h.Free();
            callback(result, ref lobby);
        }

        public void ConnectLobby(int lobbyId, String secret, ConnectLobbyHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.ConnectLobby(MethodsPtr, lobbyId, secret, GCHandle.ToIntPtr(wrapped), ConnectLobbyCallbackImpl);
        }

        [MonoPInvokeCallback]
        private static void ConnectLobbyWithActivitySecretCallbackImpl(IntPtr ptr, Result result, ref Lobby lobby)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            ConnectLobbyWithActivitySecretHandler callback = (ConnectLobbyWithActivitySecretHandler)h.Target;
            h.Free();
            callback(result, ref lobby);
        }

        public void ConnectLobbyWithActivitySecret(String activitySecret, ConnectLobbyWithActivitySecretHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.ConnectLobbyWithActivitySecret(MethodsPtr, activitySecret, GCHandle.ToIntPtr(wrapped), ConnectLobbyWithActivitySecretCallbackImpl);
        }

        [MonoPInvokeCallback]
        private static void DisconnectLobbyCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            DisconnectLobbyHandler callback = (DisconnectLobbyHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void DisconnectLobby(int lobbyId, DisconnectLobbyHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.DisconnectLobby(MethodsPtr, lobbyId, GCHandle.ToIntPtr(wrapped), DisconnectLobbyCallbackImpl);
        }

        public Lobby GetLobby(int lobbyId)
        {
            var ret = new Lobby();
            var res = Methods.GetLobby(MethodsPtr, lobbyId, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        public String GetLobbyActivitySecret(int lobbyId)
        {
            var ret = new StringBuilder(128);
            var res = Methods.GetLobbyActivitySecret(MethodsPtr, lobbyId, ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret.ToString();
        }

        public String GetLobbyMetadataValue(int lobbyId, String key)
        {
            var ret = new StringBuilder(4096);
            var res = Methods.GetLobbyMetadataValue(MethodsPtr, lobbyId, key, ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret.ToString();
        }

        public String GetLobbyMetadataKey(int lobbyId, Int32 index)
        {
            var ret = new StringBuilder(256);
            var res = Methods.GetLobbyMetadataKey(MethodsPtr, lobbyId, index, ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret.ToString();
        }

        public Int32 LobbyMetadataCount(int lobbyId)
        {
            var ret = new Int32();
            var res = Methods.LobbyMetadataCount(MethodsPtr, lobbyId, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        public Int32 MemberCount(int lobbyId)
        {
            var ret = new Int32();
            var res = Methods.MemberCount(MethodsPtr, lobbyId, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        public int GetMemberUserId(int lobbyId, Int32 index)
        {
            var ret = new int();
            var res = Methods.GetMemberUserId(MethodsPtr, lobbyId, index, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        public User GetMemberUser(int lobbyId, int userId)
        {
            var ret = new User();
            var res = Methods.GetMemberUser(MethodsPtr, lobbyId, userId, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        public String GetMemberMetadataValue(int lobbyId, int userId, String key)
        {
            var ret = new StringBuilder(4096);
            var res = Methods.GetMemberMetadataValue(MethodsPtr, lobbyId, userId, key, ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret.ToString();
        }

        public String GetMemberMetadataKey(int lobbyId, int userId, Int32 index)
        {
            var ret = new StringBuilder(256);
            var res = Methods.GetMemberMetadataKey(MethodsPtr, lobbyId, userId, index, ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret.ToString();
        }

        public Int32 MemberMetadataCount(int lobbyId, int userId)
        {
            var ret = new Int32();
            var res = Methods.MemberMetadataCount(MethodsPtr, lobbyId, userId, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        [MonoPInvokeCallback]
        private static void UpdateMemberCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            UpdateMemberHandler callback = (UpdateMemberHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void UpdateMember(int lobbyId, int userId, LobbyMemberTransaction transaction, UpdateMemberHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.UpdateMember(MethodsPtr, lobbyId, userId, transaction.MethodsPtr, GCHandle.ToIntPtr(wrapped), UpdateMemberCallbackImpl);
            transaction.MethodsPtr = IntPtr.Zero;
        }

        [MonoPInvokeCallback]
        private static void SendLobbyMessageCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            SendLobbyMessageHandler callback = (SendLobbyMessageHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void SendLobbyMessage(int lobbyId, byte[] data, SendLobbyMessageHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.SendLobbyMessage(MethodsPtr, lobbyId, data, data.Length, GCHandle.ToIntPtr(wrapped), SendLobbyMessageCallbackImpl);
        }

        public LobbySearchQuery GetSearchQuery()
        {
            var ret = new LobbySearchQuery();
            var res = Methods.GetSearchQuery(MethodsPtr, ref ret.MethodsPtr);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        [MonoPInvokeCallback]
        private static void SearchCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            SearchHandler callback = (SearchHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void Search(LobbySearchQuery query, SearchHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.Search(MethodsPtr, query.MethodsPtr, GCHandle.ToIntPtr(wrapped), SearchCallbackImpl);
            query.MethodsPtr = IntPtr.Zero;
        }

        public Int32 LobbyCount()
        {
            var ret = new Int32();
            Methods.LobbyCount(MethodsPtr, ref ret);
            return ret;
        }

        public int GetLobbyId(Int32 index)
        {
            var ret = new int();
            var res = Methods.GetLobbyId(MethodsPtr, index, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        [MonoPInvokeCallback]
        private static void ConnectVoiceCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            ConnectVoiceHandler callback = (ConnectVoiceHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void ConnectVoice(int lobbyId, ConnectVoiceHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.ConnectVoice(MethodsPtr, lobbyId, GCHandle.ToIntPtr(wrapped), ConnectVoiceCallbackImpl);
        }

        [MonoPInvokeCallback]
        private static void DisconnectVoiceCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            DisconnectVoiceHandler callback = (DisconnectVoiceHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void DisconnectVoice(int lobbyId, DisconnectVoiceHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.DisconnectVoice(MethodsPtr, lobbyId, GCHandle.ToIntPtr(wrapped), DisconnectVoiceCallbackImpl);
        }

        public void ConnectNetwork(int lobbyId)
        {
            var res = Methods.ConnectNetwork(MethodsPtr, lobbyId);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
        }

        public void DisconnectNetwork(int lobbyId)
        {
            var res = Methods.DisconnectNetwork(MethodsPtr, lobbyId);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
        }

        public void FlushNetwork()
        {
            var res = Methods.FlushNetwork(MethodsPtr);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
        }

        public void OpenNetworkChannel(int lobbyId, byte channelId, boolean reliable)
        {
            var res = Methods.OpenNetworkChannel(MethodsPtr, lobbyId, channelId, reliable);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
        }

        public void SendNetworkMessage(int lobbyId, int userId, byte channelId, byte[] data)
        {
            var res = Methods.SendNetworkMessage(MethodsPtr, lobbyId, userId, channelId, data, data.Length);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
        }

        [MonoPInvokeCallback]
        private static void OnLobbyUpdateImpl(IntPtr ptr, int lobbyId)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            Discord d = (Discord)h.Target;
            if (d.LobbyManagerInstance.OnLobbyUpdate != null)
            {
                d.LobbyManagerInstance.OnLobbyUpdate.Invoke(lobbyId);
            }
        }

        [MonoPInvokeCallback]
        private static void OnLobbyDeleteImpl(IntPtr ptr, int lobbyId, long reason)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            Discord d = (Discord)h.Target;
            if (d.LobbyManagerInstance.OnLobbyDelete != null)
            {
                d.LobbyManagerInstance.OnLobbyDelete.Invoke(lobbyId, reason);
            }
        }

        [MonoPInvokeCallback]
        private static void OnMemberConnectImpl(IntPtr ptr, int lobbyId, int userId)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            Discord d = (Discord)h.Target;
            if (d.LobbyManagerInstance.OnMemberConnect != null)
            {
                d.LobbyManagerInstance.OnMemberConnect.Invoke(lobbyId, userId);
            }
        }

        [MonoPInvokeCallback]
        private static void OnMemberUpdateImpl(IntPtr ptr, int lobbyId, int userId)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            Discord d = (Discord)h.Target;
            if (d.LobbyManagerInstance.OnMemberUpdate != null)
            {
                d.LobbyManagerInstance.OnMemberUpdate.Invoke(lobbyId, userId);
            }
        }

        [MonoPInvokeCallback]
        private static void OnMemberDisconnectImpl(IntPtr ptr, int lobbyId, int userId)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            Discord d = (Discord)h.Target;
            if (d.LobbyManagerInstance.OnMemberDisconnect != null)
            {
                d.LobbyManagerInstance.OnMemberDisconnect.Invoke(lobbyId, userId);
            }
        }

        [MonoPInvokeCallback]
        private static void OnLobbyMessageImpl(IntPtr ptr, int lobbyId, int userId, IntPtr dataPtr, Int32 dataLen)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            Discord d = (Discord)h.Target;
            if (d.LobbyManagerInstance.OnLobbyMessage != null)
            {
                byte[] data = new byte[dataLen];
                Marshal.Copy(dataPtr, data, 0, (int)dataLen);
                d.LobbyManagerInstance.OnLobbyMessage.Invoke(lobbyId, userId, data);
            }
        }

        [MonoPInvokeCallback]
        private static void OnSpeakingImpl(IntPtr ptr, int lobbyId, int userId, boolean speaking)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            Discord d = (Discord)h.Target;
            if (d.LobbyManagerInstance.OnSpeaking != null)
            {
                d.LobbyManagerInstance.OnSpeaking.Invoke(lobbyId, userId, speaking);
            }
        }

        [MonoPInvokeCallback]
        private static void OnNetworkMessageImpl(IntPtr ptr, int lobbyId, int userId, byte channelId, IntPtr dataPtr, Int32 dataLen)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            Discord d = (Discord)h.Target;
            if (d.LobbyManagerInstance.OnNetworkMessage != null)
            {
                byte[] data = new byte[dataLen];
                Marshal.Copy(dataPtr, data, 0, (int)dataLen);
                d.LobbyManagerInstance.OnNetworkMessage.Invoke(lobbyId, userId, channelId, data);
            }
        }
    }

    public partial class NetworkManager
    {
        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIEvents
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void MessageHandler(IntPtr ptr, long peerId, byte channelId, IntPtr dataPtr, Int32 dataLen);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void RouteUpdateHandler(IntPtr ptr, [MarshalAs(UnmanagedType.LPStr)]String routeData);

            internal MessageHandler OnMessage;

            internal RouteUpdateHandler OnRouteUpdate;
        }

        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIMethods
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void GetPeerIdMethod(IntPtr methodsPtr, ref long peerId);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result FlushMethod(IntPtr methodsPtr);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result OpenPeerMethod(IntPtr methodsPtr, long peerId, [MarshalAs(UnmanagedType.LPStr)]String routeData);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result UpdatePeerMethod(IntPtr methodsPtr, long peerId, [MarshalAs(UnmanagedType.LPStr)]String routeData);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result ClosePeerMethod(IntPtr methodsPtr, long peerId);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result OpenChannelMethod(IntPtr methodsPtr, long peerId, byte channelId, boolean reliable);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result CloseChannelMethod(IntPtr methodsPtr, long peerId, byte channelId);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result SendMessageMethod(IntPtr methodsPtr, long peerId, byte channelId, byte[] data, Int32 dataLen);

            internal GetPeerIdMethod GetPeerId;

            internal FlushMethod Flush;

            internal OpenPeerMethod OpenPeer;

            internal UpdatePeerMethod UpdatePeer;

            internal ClosePeerMethod ClosePeer;

            internal OpenChannelMethod OpenChannel;

            internal CloseChannelMethod CloseChannel;

            internal SendMessageMethod SendMessage;
        }

        public delegate void MessageHandler(long peerId, byte channelId, byte[] data);

        public delegate void RouteUpdateHandler(String routeData);

        private IntPtr MethodsPtr;

        private Object MethodsStructure;

        private FFIMethods Methods
        {
            get
            {
                if (MethodsStructure == null)
                {
                    MethodsStructure = Marshal.PtrToStructure(MethodsPtr, typeof(FFIMethods));
                }
                return (FFIMethods)MethodsStructure;
            }

        }

        public event MessageHandler OnMessage;

        public event RouteUpdateHandler OnRouteUpdate;

        internal NetworkManager(IntPtr ptr, IntPtr eventsPtr, ref FFIEvents events)
        {
            if (eventsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
            InitEvents(eventsPtr, ref events);
            MethodsPtr = ptr;
            if (MethodsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
        }

        private void InitEvents(IntPtr eventsPtr, ref FFIEvents events)
        {
            events.OnMessage = OnMessageImpl;
            events.OnRouteUpdate = OnRouteUpdateImpl;
            Marshal.StructureToPtr(events, eventsPtr, false);
        }

        /// <summary>
        /// Get the local peer ID for this process.
        /// </summary>
        public long GetPeerId()
        {
            var ret = new long();
            Methods.GetPeerId(MethodsPtr, ref ret);
            return ret;
        }

        /// <summary>
        /// Send pending network messages.
        /// </summary>
        public void Flush()
        {
            var res = Methods.Flush(MethodsPtr);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
        }

        /// <summary>
        /// Open a connection to a remote peer.
        /// </summary>
        public void OpenPeer(long peerId, String routeData)
        {
            var res = Methods.OpenPeer(MethodsPtr, peerId, routeData);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
        }

        /// <summary>
        /// Update the route data for a connected peer.
        /// </summary>
        public void UpdatePeer(long peerId, String routeData)
        {
            var res = Methods.UpdatePeer(MethodsPtr, peerId, routeData);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
        }

        /// <summary>
        /// Close the connection to a remote peer.
        /// </summary>
        public void ClosePeer(long peerId)
        {
            var res = Methods.ClosePeer(MethodsPtr, peerId);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
        }

        /// <summary>
        /// Open a message channel to a connected peer.
        /// </summary>
        public void OpenChannel(long peerId, byte channelId, boolean reliable)
        {
            var res = Methods.OpenChannel(MethodsPtr, peerId, channelId, reliable);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
        }

        /// <summary>
        /// Close a message channel to a connected peer.
        /// </summary>
        public void CloseChannel(long peerId, byte channelId)
        {
            var res = Methods.CloseChannel(MethodsPtr, peerId, channelId);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
        }

        /// <summary>
        /// Send a message to a connected peer over an opened message channel.
        /// </summary>
        public void SendMessage(long peerId, byte channelId, byte[] data)
        {
            var res = Methods.SendMessage(MethodsPtr, peerId, channelId, data, data.Length);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
        }

        [MonoPInvokeCallback]
        private static void OnMessageImpl(IntPtr ptr, long peerId, byte channelId, IntPtr dataPtr, Int32 dataLen)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            Discord d = (Discord)h.Target;
            if (d.NetworkManagerInstance.OnMessage != null)
            {
                byte[] data = new byte[dataLen];
                Marshal.Copy(dataPtr, data, 0, (int)dataLen);
                d.NetworkManagerInstance.OnMessage.Invoke(peerId, channelId, data);
            }
        }

        [MonoPInvokeCallback]
        private static void OnRouteUpdateImpl(IntPtr ptr, String routeData)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            Discord d = (Discord)h.Target;
            if (d.NetworkManagerInstance.OnRouteUpdate != null)
            {
                d.NetworkManagerInstance.OnRouteUpdate.Invoke(routeData);
            }
        }
    }

    public partial class OverlayManager
    {
        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIEvents
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void ToggleHandler(IntPtr ptr, boolean locked);

            internal ToggleHandler OnToggle;
        }

        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIMethods
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void IsEnabledMethod(IntPtr methodsPtr, ref boolean enabled);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void IsLockedMethod(IntPtr methodsPtr, ref boolean locked);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void SetLockedCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void SetLockedMethod(IntPtr methodsPtr, boolean locked, IntPtr callbackData, SetLockedCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void OpenActivityInviteCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void OpenActivityInviteMethod(IntPtr methodsPtr, ActivityActionType type, IntPtr callbackData, OpenActivityInviteCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void OpenGuildInviteCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void OpenGuildInviteMethod(IntPtr methodsPtr, [MarshalAs(UnmanagedType.LPStr)]String code, IntPtr callbackData, OpenGuildInviteCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void OpenVoiceSettingsCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void OpenVoiceSettingsMethod(IntPtr methodsPtr, IntPtr callbackData, OpenVoiceSettingsCallback callback);

            internal IsEnabledMethod IsEnabled;

            internal IsLockedMethod IsLocked;

            internal SetLockedMethod SetLocked;

            internal OpenActivityInviteMethod OpenActivityInvite;

            internal OpenGuildInviteMethod OpenGuildInvite;

            internal OpenVoiceSettingsMethod OpenVoiceSettings;
        }

        public delegate void SetLockedHandler(Result result);

        public delegate void OpenActivityInviteHandler(Result result);

        public delegate void OpenGuildInviteHandler(Result result);

        public delegate void OpenVoiceSettingsHandler(Result result);

        public delegate void ToggleHandler(boolean locked);

        private IntPtr MethodsPtr;

        private Object MethodsStructure;

        private FFIMethods Methods
        {
            get
            {
                if (MethodsStructure == null)
                {
                    MethodsStructure = Marshal.PtrToStructure(MethodsPtr, typeof(FFIMethods));
                }
                return (FFIMethods)MethodsStructure;
            }

        }

        public event ToggleHandler OnToggle;

        internal OverlayManager(IntPtr ptr, IntPtr eventsPtr, ref FFIEvents events)
        {
            if (eventsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
            InitEvents(eventsPtr, ref events);
            MethodsPtr = ptr;
            if (MethodsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
        }

        private void InitEvents(IntPtr eventsPtr, ref FFIEvents events)
        {
            events.OnToggle = OnToggleImpl;
            Marshal.StructureToPtr(events, eventsPtr, false);
        }

        public boolean IsEnabled()
        {
            var ret = new boolean();
            Methods.IsEnabled(MethodsPtr, ref ret);
            return ret;
        }

        public boolean IsLocked()
        {
            var ret = new boolean();
            Methods.IsLocked(MethodsPtr, ref ret);
            return ret;
        }

        [MonoPInvokeCallback]
        private static void SetLockedCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            SetLockedHandler callback = (SetLockedHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void SetLocked(boolean locked, SetLockedHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.SetLocked(MethodsPtr, locked, GCHandle.ToIntPtr(wrapped), SetLockedCallbackImpl);
        }

        [MonoPInvokeCallback]
        private static void OpenActivityInviteCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            OpenActivityInviteHandler callback = (OpenActivityInviteHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void OpenActivityInvite(ActivityActionType type, OpenActivityInviteHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.OpenActivityInvite(MethodsPtr, type, GCHandle.ToIntPtr(wrapped), OpenActivityInviteCallbackImpl);
        }

        [MonoPInvokeCallback]
        private static void OpenGuildInviteCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            OpenGuildInviteHandler callback = (OpenGuildInviteHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void OpenGuildInvite(String code, OpenGuildInviteHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.OpenGuildInvite(MethodsPtr, code, GCHandle.ToIntPtr(wrapped), OpenGuildInviteCallbackImpl);
        }

        [MonoPInvokeCallback]
        private static void OpenVoiceSettingsCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            OpenVoiceSettingsHandler callback = (OpenVoiceSettingsHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void OpenVoiceSettings(OpenVoiceSettingsHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.OpenVoiceSettings(MethodsPtr, GCHandle.ToIntPtr(wrapped), OpenVoiceSettingsCallbackImpl);
        }

        [MonoPInvokeCallback]
        private static void OnToggleImpl(IntPtr ptr, boolean locked)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            Discord d = (Discord)h.Target;
            if (d.OverlayManagerInstance.OnToggle != null)
            {
                d.OverlayManagerInstance.OnToggle.Invoke(locked);
            }
        }
    }

    public partial class StorageManager
    {
        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIEvents
        {

        }

        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIMethods
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result ReadMethod(IntPtr methodsPtr, [MarshalAs(UnmanagedType.LPStr)]String name, byte[] data, Int32 dataLen, ref long read);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void ReadAsyncCallback(IntPtr ptr, Result result, IntPtr dataPtr, Int32 dataLen);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void ReadAsyncMethod(IntPtr methodsPtr, [MarshalAs(UnmanagedType.LPStr)]String name, IntPtr callbackData, ReadAsyncCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void ReadAsyncPartialCallback(IntPtr ptr, Result result, IntPtr dataPtr, Int32 dataLen);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void ReadAsyncPartialMethod(IntPtr methodsPtr, [MarshalAs(UnmanagedType.LPStr)]String name, long offset, long length, IntPtr callbackData, ReadAsyncPartialCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result WriteMethod(IntPtr methodsPtr, [MarshalAs(UnmanagedType.LPStr)]String name, byte[] data, Int32 dataLen);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void WriteAsyncCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void WriteAsyncMethod(IntPtr methodsPtr, [MarshalAs(UnmanagedType.LPStr)]String name, byte[] data, Int32 dataLen, IntPtr callbackData, WriteAsyncCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result DeleteMethod(IntPtr methodsPtr, [MarshalAs(UnmanagedType.LPStr)]String name);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result ExistsMethod(IntPtr methodsPtr, [MarshalAs(UnmanagedType.LPStr)]String name, ref boolean exists);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void CountMethod(IntPtr methodsPtr, ref Int32 count);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result StatMethod(IntPtr methodsPtr, [MarshalAs(UnmanagedType.LPStr)]String name, ref FileStat stat);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result StatAtMethod(IntPtr methodsPtr, Int32 index, ref FileStat stat);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetPathMethod(IntPtr methodsPtr, StringBuilder path);

            internal ReadMethod Read;

            internal ReadAsyncMethod ReadAsync;

            internal ReadAsyncPartialMethod ReadAsyncPartial;

            internal WriteMethod Write;

            internal WriteAsyncMethod WriteAsync;

            internal DeleteMethod Delete;

            internal ExistsMethod Exists;

            internal CountMethod Count;

            internal StatMethod Stat;

            internal StatAtMethod StatAt;

            internal GetPathMethod GetPath;
        }

        public delegate void ReadAsyncHandler(Result result, byte[] data);

        public delegate void ReadAsyncPartialHandler(Result result, byte[] data);

        public delegate void WriteAsyncHandler(Result result);

        private IntPtr MethodsPtr;

        private Object MethodsStructure;

        private FFIMethods Methods
        {
            get
            {
                if (MethodsStructure == null)
                {
                    MethodsStructure = Marshal.PtrToStructure(MethodsPtr, typeof(FFIMethods));
                }
                return (FFIMethods)MethodsStructure;
            }

        }

        internal StorageManager(IntPtr ptr, IntPtr eventsPtr, ref FFIEvents events)
        {
            if (eventsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
            InitEvents(eventsPtr, ref events);
            MethodsPtr = ptr;
            if (MethodsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
        }

        private void InitEvents(IntPtr eventsPtr, ref FFIEvents events)
        {
            Marshal.StructureToPtr(events, eventsPtr, false);
        }

        public long Read(String name, byte[] data)
        {
            var ret = new long();
            var res = Methods.Read(MethodsPtr, name, data, data.Length, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        [MonoPInvokeCallback]
        private static void ReadAsyncCallbackImpl(IntPtr ptr, Result result, IntPtr dataPtr, Int32 dataLen)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            ReadAsyncHandler callback = (ReadAsyncHandler)h.Target;
            h.Free();
            byte[] data = new byte[dataLen];
            Marshal.Copy(dataPtr, data, 0, (int)dataLen);
            callback(result, data);
        }

        public void ReadAsync(String name, ReadAsyncHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.ReadAsync(MethodsPtr, name, GCHandle.ToIntPtr(wrapped), ReadAsyncCallbackImpl);
        }

        [MonoPInvokeCallback]
        private static void ReadAsyncPartialCallbackImpl(IntPtr ptr, Result result, IntPtr dataPtr, Int32 dataLen)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            ReadAsyncPartialHandler callback = (ReadAsyncPartialHandler)h.Target;
            h.Free();
            byte[] data = new byte[dataLen];
            Marshal.Copy(dataPtr, data, 0, (int)dataLen);
            callback(result, data);
        }

        public void ReadAsyncPartial(String name, long offset, long length, ReadAsyncPartialHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.ReadAsyncPartial(MethodsPtr, name, offset, length, GCHandle.ToIntPtr(wrapped), ReadAsyncPartialCallbackImpl);
        }

        public void Write(String name, byte[] data)
        {
            var res = Methods.Write(MethodsPtr, name, data, data.Length);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
        }

        [MonoPInvokeCallback]
        private static void WriteAsyncCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            WriteAsyncHandler callback = (WriteAsyncHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void WriteAsync(String name, byte[] data, WriteAsyncHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.WriteAsync(MethodsPtr, name, data, data.Length, GCHandle.ToIntPtr(wrapped), WriteAsyncCallbackImpl);
        }

        public void Delete(String name)
        {
            var res = Methods.Delete(MethodsPtr, name);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
        }

        public boolean Exists(String name)
        {
            var ret = new boolean();
            var res = Methods.Exists(MethodsPtr, name, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        public Int32 Count()
        {
            var ret = new Int32();
            Methods.Count(MethodsPtr, ref ret);
            return ret;
        }

        public FileStat Stat(String name)
        {
            var ret = new FileStat();
            var res = Methods.Stat(MethodsPtr, name, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        public FileStat StatAt(Int32 index)
        {
            var ret = new FileStat();
            var res = Methods.StatAt(MethodsPtr, index, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        public String GetPath()
        {
            var ret = new StringBuilder(4096);
            var res = Methods.GetPath(MethodsPtr, ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret.ToString();
        }
    }

    public partial class StoreManager
    {
        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIEvents
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void EntitlementCreateHandler(IntPtr ptr, ref Entitlement entitlement);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void EntitlementDeleteHandler(IntPtr ptr, ref Entitlement entitlement);

            internal EntitlementCreateHandler OnEntitlementCreate;

            internal EntitlementDeleteHandler OnEntitlementDelete;
        }

        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIMethods
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void FetchSkusCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void FetchSkusMethod(IntPtr methodsPtr, IntPtr callbackData, FetchSkusCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void CountSkusMethod(IntPtr methodsPtr, ref Int32 count);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetSkuMethod(IntPtr methodsPtr, int skuId, ref Sku sku);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetSkuAtMethod(IntPtr methodsPtr, Int32 index, ref Sku sku);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void FetchEntitlementsCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void FetchEntitlementsMethod(IntPtr methodsPtr, IntPtr callbackData, FetchEntitlementsCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void CountEntitlementsMethod(IntPtr methodsPtr, ref Int32 count);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetEntitlementMethod(IntPtr methodsPtr, int entitlementId, ref Entitlement entitlement);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetEntitlementAtMethod(IntPtr methodsPtr, Int32 index, ref Entitlement entitlement);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result HasSkuEntitlementMethod(IntPtr methodsPtr, int skuId, ref boolean hasEntitlement);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void StartPurchaseCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void StartPurchaseMethod(IntPtr methodsPtr, int skuId, IntPtr callbackData, StartPurchaseCallback callback);

            internal FetchSkusMethod FetchSkus;

            internal CountSkusMethod CountSkus;

            internal GetSkuMethod GetSku;

            internal GetSkuAtMethod GetSkuAt;

            internal FetchEntitlementsMethod FetchEntitlements;

            internal CountEntitlementsMethod CountEntitlements;

            internal GetEntitlementMethod GetEntitlement;

            internal GetEntitlementAtMethod GetEntitlementAt;

            internal HasSkuEntitlementMethod HasSkuEntitlement;

            internal StartPurchaseMethod StartPurchase;
        }

        public delegate void FetchSkusHandler(Result result);

        public delegate void FetchEntitlementsHandler(Result result);

        public delegate void StartPurchaseHandler(Result result);

        public delegate void EntitlementCreateHandler(ref Entitlement entitlement);

        public delegate void EntitlementDeleteHandler(ref Entitlement entitlement);

        private IntPtr MethodsPtr;

        private Object MethodsStructure;

        private FFIMethods Methods
        {
            get
            {
                if (MethodsStructure == null)
                {
                    MethodsStructure = Marshal.PtrToStructure(MethodsPtr, typeof(FFIMethods));
                }
                return (FFIMethods)MethodsStructure;
            }

        }

        public event EntitlementCreateHandler OnEntitlementCreate;

        public event EntitlementDeleteHandler OnEntitlementDelete;

        internal StoreManager(IntPtr ptr, IntPtr eventsPtr, ref FFIEvents events)
        {
            if (eventsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
            InitEvents(eventsPtr, ref events);
            MethodsPtr = ptr;
            if (MethodsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
        }

        private void InitEvents(IntPtr eventsPtr, ref FFIEvents events)
        {
            events.OnEntitlementCreate = OnEntitlementCreateImpl;
            events.OnEntitlementDelete = OnEntitlementDeleteImpl;
            Marshal.StructureToPtr(events, eventsPtr, false);
        }

        [MonoPInvokeCallback]
        private static void FetchSkusCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            FetchSkusHandler callback = (FetchSkusHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void FetchSkus(FetchSkusHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.FetchSkus(MethodsPtr, GCHandle.ToIntPtr(wrapped), FetchSkusCallbackImpl);
        }

        public Int32 CountSkus()
        {
            var ret = new Int32();
            Methods.CountSkus(MethodsPtr, ref ret);
            return ret;
        }

        public Sku GetSku(int skuId)
        {
            var ret = new Sku();
            var res = Methods.GetSku(MethodsPtr, skuId, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        public Sku GetSkuAt(Int32 index)
        {
            var ret = new Sku();
            var res = Methods.GetSkuAt(MethodsPtr, index, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        [MonoPInvokeCallback]
        private static void FetchEntitlementsCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            FetchEntitlementsHandler callback = (FetchEntitlementsHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void FetchEntitlements(FetchEntitlementsHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.FetchEntitlements(MethodsPtr, GCHandle.ToIntPtr(wrapped), FetchEntitlementsCallbackImpl);
        }

        public Int32 CountEntitlements()
        {
            var ret = new Int32();
            Methods.CountEntitlements(MethodsPtr, ref ret);
            return ret;
        }

        public Entitlement GetEntitlement(int entitlementId)
        {
            var ret = new Entitlement();
            var res = Methods.GetEntitlement(MethodsPtr, entitlementId, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        public Entitlement GetEntitlementAt(Int32 index)
        {
            var ret = new Entitlement();
            var res = Methods.GetEntitlementAt(MethodsPtr, index, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        public boolean HasSkuEntitlement(int skuId)
        {
            var ret = new boolean();
            var res = Methods.HasSkuEntitlement(MethodsPtr, skuId, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        [MonoPInvokeCallback]
        private static void StartPurchaseCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            StartPurchaseHandler callback = (StartPurchaseHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void StartPurchase(int skuId, StartPurchaseHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.StartPurchase(MethodsPtr, skuId, GCHandle.ToIntPtr(wrapped), StartPurchaseCallbackImpl);
        }

        [MonoPInvokeCallback]
        private static void OnEntitlementCreateImpl(IntPtr ptr, ref Entitlement entitlement)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            Discord d = (Discord)h.Target;
            if (d.StoreManagerInstance.OnEntitlementCreate != null)
            {
                d.StoreManagerInstance.OnEntitlementCreate.Invoke(ref entitlement);
            }
        }

        [MonoPInvokeCallback]
        private static void OnEntitlementDeleteImpl(IntPtr ptr, ref Entitlement entitlement)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            Discord d = (Discord)h.Target;
            if (d.StoreManagerInstance.OnEntitlementDelete != null)
            {
                d.StoreManagerInstance.OnEntitlementDelete.Invoke(ref entitlement);
            }
        }
    }

    public partial class VoiceManager
    {
        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIEvents
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void SettingsUpdateHandler(IntPtr ptr);

            internal SettingsUpdateHandler OnSettingsUpdate;
        }

        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIMethods
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetInputModeMethod(IntPtr methodsPtr, ref InputMode inputMode);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void SetInputModeCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void SetInputModeMethod(IntPtr methodsPtr, InputMode inputMode, IntPtr callbackData, SetInputModeCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result IsSelfMuteMethod(IntPtr methodsPtr, ref boolean mute);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result SetSelfMuteMethod(IntPtr methodsPtr, boolean mute);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result IsSelfDeafMethod(IntPtr methodsPtr, ref boolean deaf);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result SetSelfDeafMethod(IntPtr methodsPtr, boolean deaf);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result IsLocalMuteMethod(IntPtr methodsPtr, int userId, ref boolean mute);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result SetLocalMuteMethod(IntPtr methodsPtr, int userId, boolean mute);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetLocalVolumeMethod(IntPtr methodsPtr, int userId, ref byte volume);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result SetLocalVolumeMethod(IntPtr methodsPtr, int userId, byte volume);

            internal GetInputModeMethod GetInputMode;

            internal SetInputModeMethod SetInputMode;

            internal IsSelfMuteMethod IsSelfMute;

            internal SetSelfMuteMethod SetSelfMute;

            internal IsSelfDeafMethod IsSelfDeaf;

            internal SetSelfDeafMethod SetSelfDeaf;

            internal IsLocalMuteMethod IsLocalMute;

            internal SetLocalMuteMethod SetLocalMute;

            internal GetLocalVolumeMethod GetLocalVolume;

            internal SetLocalVolumeMethod SetLocalVolume;
        }

        public delegate void SetInputModeHandler(Result result);

        public delegate void SettingsUpdateHandler();

        private IntPtr MethodsPtr;

        private Object MethodsStructure;

        private FFIMethods Methods
        {
            get
            {
                if (MethodsStructure == null)
                {
                    MethodsStructure = Marshal.PtrToStructure(MethodsPtr, typeof(FFIMethods));
                }
                return (FFIMethods)MethodsStructure;
            }

        }

        public event SettingsUpdateHandler OnSettingsUpdate;

        internal VoiceManager(IntPtr ptr, IntPtr eventsPtr, ref FFIEvents events)
        {
            if (eventsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
            InitEvents(eventsPtr, ref events);
            MethodsPtr = ptr;
            if (MethodsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
        }

        private void InitEvents(IntPtr eventsPtr, ref FFIEvents events)
        {
            events.OnSettingsUpdate = OnSettingsUpdateImpl;
            Marshal.StructureToPtr(events, eventsPtr, false);
        }

        public InputMode GetInputMode()
        {
            var ret = new InputMode();
            var res = Methods.GetInputMode(MethodsPtr, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        [MonoPInvokeCallback]
        private static void SetInputModeCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            SetInputModeHandler callback = (SetInputModeHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void SetInputMode(InputMode inputMode, SetInputModeHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.SetInputMode(MethodsPtr, inputMode, GCHandle.ToIntPtr(wrapped), SetInputModeCallbackImpl);
        }

        public boolean IsSelfMute()
        {
            var ret = new boolean();
            var res = Methods.IsSelfMute(MethodsPtr, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        public void SetSelfMute(boolean mute)
        {
            var res = Methods.SetSelfMute(MethodsPtr, mute);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
        }

        public boolean IsSelfDeaf()
        {
            var ret = new boolean();
            var res = Methods.IsSelfDeaf(MethodsPtr, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        public void SetSelfDeaf(boolean deaf)
        {
            var res = Methods.SetSelfDeaf(MethodsPtr, deaf);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
        }

        public boolean IsLocalMute(int userId)
        {
            var ret = new boolean();
            var res = Methods.IsLocalMute(MethodsPtr, userId, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        public void SetLocalMute(int userId, boolean mute)
        {
            var res = Methods.SetLocalMute(MethodsPtr, userId, mute);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
        }

        public byte GetLocalVolume(int userId)
        {
            var ret = new byte();
            var res = Methods.GetLocalVolume(MethodsPtr, userId, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        public void SetLocalVolume(int userId, byte volume)
        {
            var res = Methods.SetLocalVolume(MethodsPtr, userId, volume);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
        }

        [MonoPInvokeCallback]
        private static void OnSettingsUpdateImpl(IntPtr ptr)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            Discord d = (Discord)h.Target;
            if (d.VoiceManagerInstance.OnSettingsUpdate != null)
            {
                d.VoiceManagerInstance.OnSettingsUpdate.Invoke();
            }
        }
    }

    public partial class AchievementManager
    {
        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIEvents
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void UserAchievementUpdateHandler(IntPtr ptr, ref UserAchievement userAchievement);

            internal UserAchievementUpdateHandler OnUserAchievementUpdate;
        }

        [StructLayout(LayoutKind.Sequential)]
        internal partial struct FFIMethods
        {
            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void SetUserAchievementCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void SetUserAchievementMethod(IntPtr methodsPtr, int achievementId, byte percentComplete, IntPtr callbackData, SetUserAchievementCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void FetchUserAchievementsCallback(IntPtr ptr, Result result);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void FetchUserAchievementsMethod(IntPtr methodsPtr, IntPtr callbackData, FetchUserAchievementsCallback callback);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate void CountUserAchievementsMethod(IntPtr methodsPtr, ref Int32 count);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetUserAchievementMethod(IntPtr methodsPtr, int userAchievementId, ref UserAchievement userAchievement);

            [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
            internal delegate Result GetUserAchievementAtMethod(IntPtr methodsPtr, Int32 index, ref UserAchievement userAchievement);

            internal SetUserAchievementMethod SetUserAchievement;

            internal FetchUserAchievementsMethod FetchUserAchievements;

            internal CountUserAchievementsMethod CountUserAchievements;

            internal GetUserAchievementMethod GetUserAchievement;

            internal GetUserAchievementAtMethod GetUserAchievementAt;
        }

        public delegate void SetUserAchievementHandler(Result result);

        public delegate void FetchUserAchievementsHandler(Result result);

        public delegate void UserAchievementUpdateHandler(ref UserAchievement userAchievement);

        private IntPtr MethodsPtr;

        private Object MethodsStructure;

        private FFIMethods Methods
        {
            get
            {
                if (MethodsStructure == null)
                {
                    MethodsStructure = Marshal.PtrToStructure(MethodsPtr, typeof(FFIMethods));
                }
                return (FFIMethods)MethodsStructure;
            }

        }

        public event UserAchievementUpdateHandler OnUserAchievementUpdate;

        internal AchievementManager(IntPtr ptr, IntPtr eventsPtr, ref FFIEvents events)
        {
            if (eventsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
            InitEvents(eventsPtr, ref events);
            MethodsPtr = ptr;
            if (MethodsPtr == IntPtr.Zero) {
                throw new ResultException(Result.InternalError);
            }
        }

        private void InitEvents(IntPtr eventsPtr, ref FFIEvents events)
        {
            events.OnUserAchievementUpdate = OnUserAchievementUpdateImpl;
            Marshal.StructureToPtr(events, eventsPtr, false);
        }

        [MonoPInvokeCallback]
        private static void SetUserAchievementCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            SetUserAchievementHandler callback = (SetUserAchievementHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void SetUserAchievement(int achievementId, byte percentComplete, SetUserAchievementHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.SetUserAchievement(MethodsPtr, achievementId, percentComplete, GCHandle.ToIntPtr(wrapped), SetUserAchievementCallbackImpl);
        }

        [MonoPInvokeCallback]
        private static void FetchUserAchievementsCallbackImpl(IntPtr ptr, Result result)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            FetchUserAchievementsHandler callback = (FetchUserAchievementsHandler)h.Target;
            h.Free();
            callback(result);
        }

        public void FetchUserAchievements(FetchUserAchievementsHandler callback)
        {
            GCHandle wrapped = GCHandle.Alloc(callback);
            Methods.FetchUserAchievements(MethodsPtr, GCHandle.ToIntPtr(wrapped), FetchUserAchievementsCallbackImpl);
        }

        public Int32 CountUserAchievements()
        {
            var ret = new Int32();
            Methods.CountUserAchievements(MethodsPtr, ref ret);
            return ret;
        }

        public UserAchievement GetUserAchievement(int userAchievementId)
        {
            var ret = new UserAchievement();
            var res = Methods.GetUserAchievement(MethodsPtr, userAchievementId, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        public UserAchievement GetUserAchievementAt(Int32 index)
        {
            var ret = new UserAchievement();
            var res = Methods.GetUserAchievementAt(MethodsPtr, index, ref ret);
            if (res != Result.Ok)
            {
                throw new ResultException(res);
            }
            return ret;
        }

        [MonoPInvokeCallback]
        private static void OnUserAchievementUpdateImpl(IntPtr ptr, ref UserAchievement userAchievement)
        {
            GCHandle h = GCHandle.FromIntPtr(ptr);
            Discord d = (Discord)h.Target;
            if (d.AchievementManagerInstance.OnUserAchievementUpdate != null)
            {
                d.AchievementManagerInstance.OnUserAchievementUpdate.Invoke(ref userAchievement);
            }
        }
    }
}
