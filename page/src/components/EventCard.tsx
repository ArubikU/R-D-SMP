import React from 'react';
import { Badge } from './ui/Badge';

interface EventCardProps {
    event: {
        name: string;
        type: string;
        desc: string;
        icon: string;
    };
}

export const EventCard: React.FC<EventCardProps> = ({ event }) => (
    <div className="mc-card bg-zinc-900 p-4 hover:bg-zinc-800 transition-colors group">
        <div className="flex justify-between items-start mb-2">
            <span className="text-4xl group-hover:scale-110 transition-transform">{event.icon}</span>
            <Badge type={event.type} />
        </div>
        <h3 className="text-2xl text-white mb-1">{event.name}</h3>
        <p className="text-gray-400 text-lg leading-tight">{event.desc}</p>
    </div>
);
