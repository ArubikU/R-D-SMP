import React, { useEffect, useMemo, useState } from 'react';
import { SectionTitle } from './ui/SectionTitle';
import { EventCard } from './EventCard';
import { dailyEvents } from '../data';

interface PlayerInfo {
    name: string;
    uuid: string;
    online: boolean;
    lives: string | number;
    lives_remaining?: number;
    health?: number;
    max_health?: number;
    team?: string;
    role?: string;
}

interface ServerStatus {
    day: number;
    permadeath: boolean;
    active_modifiers: string[];
    active_day_rules?: { day: number; name: string; description: string }[];
    active_mobs?: { id: string; name: string; boss: boolean }[];
    active_mob_count?: number;
    last_mob_day?: number;
    next_event_seconds?: number;
    next_event_time?: number;
    daily_roll_odds?: {
        common: number;
        rare: number;
        epic: number;
        legendary: number;
    };
    players: PlayerInfo[];
}

export const ActiveModifiers: React.FC = () => {
    const [status, setStatus] = useState<ServerStatus | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchStatus = async () => {
            try {
                const response = await fetch('https://v0-retroproxy.vercel.app/api/proxy?url=http://151.245.32.130:25587/status');
                if (!response.ok) throw new Error('Error al conectar con el servidor');
                const data = await response.json();
                setStatus(data.data);
            } catch (err) {
                console.error(err);
                setError('No se pudo obtener el estado del servidor.');
            } finally {
                setLoading(false);
            }
        };

        fetchStatus();
        const interval = setInterval(fetchStatus, 30000); // Update every 30s
        return () => clearInterval(interval);
    }, []);

    const onlineCount = status?.players.filter((p) => p.online).length ?? 0;
    const totalPlayers = status?.players.length ?? 0;

    const formatCountdown = (seconds?: number) => {
        if (seconds === undefined || seconds < 0) return '---';
        const total = Math.floor(seconds);
        const hours = Math.floor(total / 3600);
        const minutes = Math.floor((total % 3600) / 60);
        const secs = total % 60;
        if (hours > 0) {
            return `${hours}h ${minutes.toString().padStart(2, '0')}m`;
        }
        return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    };

    const statCards = useMemo(() => {
        if (!status) {
            return [];
        }
        return [
            {
                label: 'Día Actual',
                value: status.day,
                accent: 'border-red-600',
                subtitle: `${status.active_modifiers.length} modificadores activos`,
            },
            {
                label: 'Permadeath',
                value: status.permadeath ? 'Activo' : 'Inactivo',
                accent: status.permadeath ? 'border-amber-500' : 'border-zinc-700',
                subtitle: status.permadeath ? 'Sin segundas oportunidades' : 'Respawn habilitado',
            },
            {
                label: 'Siguiente Evento',
                value: formatCountdown(status.next_event_seconds),
                accent: 'border-emerald-600',
                subtitle: 'Actualizado cada 30s',
            },
            {
                label: 'Jugadores Online',
                value: `${onlineCount} / ${totalPlayers}`,
                accent: 'border-blue-600',
                subtitle: `${onlineCount} conectados ahora mismo`,
            },
            {
                label: 'Mobs Activos',
                value: status.active_mob_count ?? (status.active_mobs?.length ?? 0),
                accent: 'border-purple-600',
                subtitle: status.last_mob_day ? `Hasta el día ${status.last_mob_day}` : 'Rotación diaria',
            },
        ];
    }, [onlineCount, status, totalPlayers]);

    const formatHearts = (player: PlayerInfo) => {
        if (!player.online) {
            return 'Offline';
        }
        if (player.health === undefined || player.max_health === undefined) {
            return 'Sin datos';
        }
        const hearts = player.health / 2;
        const maxHearts = player.max_health / 2;
        return `${hearts.toFixed(1)} ❤ / ${maxHearts.toFixed(1)} ❤`;
    };

    const formatLivesRemaining = (player: PlayerInfo) => {
        if (typeof player.lives_remaining === 'number') {
            return player.lives_remaining;
        }
        if (typeof player.lives === 'number') {
            return player.lives;
        }
        return player.lives ?? '?';
    };

    const livesNumber = (player: PlayerInfo) => {
        const val = formatLivesRemaining(player);
        if (typeof val === 'number') return val;
        const parsed = Number(val);
        return isNaN(parsed) ? null : parsed;
    };

    const cardLifeAccent = (player: PlayerInfo) => {
        const lives = livesNumber(player);
        if (lives === null) return 'border border-zinc-700';
        if (lives <= 0) return 'border-4 border-rose-700 bg-gradient-to-br from-black via-rose-950 to-black shadow-[0_0_24px_rgba(220,38,38,0.45)]';
        if (lives <= 1) return 'border-2 border-red-600 shadow-[0_0_16px_rgba(248,113,113,0.35)]';
        if (lives <= 2) return 'border-2 border-orange-500';
        return 'border border-zinc-700';
    };

    if (loading) return <div className="text-center text-gray-400 py-12">Cargando estado del servidor...</div>;
    if (error) return <div className="text-center text-red-400 py-12">{error}</div>;
    if (!status) return <div className="text-center text-gray-500 py-12">Sin datos disponibles por el momento.</div>;

    // Map active names to known events, with fallback so unknown ones still render
    const activeEvents = status.active_modifiers.map((name) =>
        dailyEvents.find((event) => event.name === name) ?? {
            name,
            type: 'Evento Activo',
            desc: 'Descripción no disponible.',
            icon: '❔',
        }
    );

    const activeMobs = status.active_mobs ?? [];

    return (
        <div className="animate-in fade-in duration-500 space-y-12">
            {/* Server Info Header */}
            <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-6">
                {statCards.map((card) => (
                    <div key={card.label} className={`bg-zinc-900/80 p-6 border-l-4 ${card.accent}`}>
                        <h3 className="text-gray-400 uppercase text-sm font-bold mb-1">{card.label}</h3>
                        <p className="text-3xl text-white font-mono">{card.value}</p>
                        {card.subtitle && <p className="text-xs text-gray-500 mt-2">{card.subtitle}</p>}
                    </div>
                ))}
            </div>

            {/* Active Modifiers */}
            <div>
                <SectionTitle>Modificadores Activos</SectionTitle>
                {activeEvents.length > 0 ? (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                        {activeEvents.map((ev, idx) => (
                            <EventCard key={idx} event={ev} />
                        ))}
                    </div>
                ) : (
                    <div className="text-center py-12 border-2 border-dashed border-gray-800 rounded-lg">
                        <span className="text-4xl block mb-2">💤</span>
                        <p className="text-gray-500">El servidor está tranquilo... por ahora.</p>
                    </div>
                )}
            </div>

            {/* Reglas Diarias Activas */}
            {status.active_day_rules && (
                <div>
                    <SectionTitle>Reglas del Día</SectionTitle>
                    {status.active_day_rules.length > 0 ? (
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
                            {status.active_day_rules.map((rule) => (
                                <div key={rule.day} className="border border-red-800/60 bg-red-950/30 p-3">
                                    <div className="flex items-center justify-between text-sm text-red-300 mb-1">
                                        <span className="font-bold">Día {rule.day}</span>
                                        <span className="text-xs uppercase tracking-wider text-red-200">Regla</span>
                                    </div>
                                    <h4 className="text-white font-semibold text-lg leading-tight">{rule.name}</h4>
                                    <p className="text-gray-300 text-sm mt-1 leading-snug">{rule.description}</p>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="text-center py-8 text-gray-500 border border-dashed border-gray-800 rounded">
                            Sin reglas especiales activas aún.
                        </div>
                    )}
                </div>
            )}

            {/* Active Mobs */}
            <div>
                <SectionTitle>Mobs Activos</SectionTitle>
                {activeMobs.length > 0 ? (
                    <div className="flex flex-wrap gap-2">
                        {activeMobs.map((mob) => (
                            <span 
                                key={mob.id} 
                                className={`px-3 py-1 rounded text-sm font-mono border ${
                                    mob.boss 
                                    ? 'bg-red-950/50 border-red-600 text-red-200 animate-pulse' 
                                    : 'bg-purple-900/30 border-purple-700/60 text-purple-200'
                                }`}
                            >
                                {mob.boss && <span className="mr-1">☠️</span>}
                                {mob.name}
                            </span>
                        ))}
                    </div>
                ) : (
                    <div className="text-center py-8 text-gray-500 border border-dashed border-gray-800 rounded">
                        Sin mobs especiales activos todavía.
                    </div>
                )}
            </div>

            {/* Player List */}
            <div>
                <SectionTitle>Jugadores</SectionTitle>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                    {status.players.map((player) => (
                        <div key={player.uuid} className={`p-4 ${cardLifeAccent(player)} ${player.online ? 'bg-zinc-900' : 'bg-zinc-950'} flex items-center gap-4`}>
                            <img 
                                src={`https://mc-heads.net/avatar/${player.name}/50`} 
                                alt={player.name} 
                                className={`w-12 h-12 rounded ${!player.online && 'grayscale'}`}
                            />
                            <div className="flex-1 min-w-0">
                                <div className="flex justify-between items-center">
                                    <h4 className={`font-bold truncate ${player.online ? 'text-white' : 'text-gray-500'}`}>{player.name}</h4>
                                    {player.online && <span className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></span>}
                                </div>
                                <div className="flex gap-2 text-xs mt-1">
                                    <span className="bg-red-900/30 text-red-300 px-1.5 py-0.5 rounded">❤️ {formatLivesRemaining(player)}</span>
                                    {player.team && <span className="bg-blue-900/30 text-blue-300 px-1.5 py-0.5 rounded">🛡️ {player.team}</span>}
                                    {player.role && <span className="bg-purple-900/30 text-purple-300 px-1.5 py-0.5 rounded">🎭 {player.role}</span>}
                                </div>
                                <div className="mt-2 text-xs text-gray-400 space-y-1">
                                    <p>Vida: {formatHearts(player)}</p>
                                    <p>Vidas restantes: {formatLivesRemaining(player)}</p>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};
